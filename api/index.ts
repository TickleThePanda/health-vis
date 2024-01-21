import bodyParser from "body-parser";

import {
  DatePutRequest,
  getAllWeight,
  saveAllWeights,
  saveWeightForPeriod as saveWeightForPeriod,
  WeightEntry,
} from "./repo/weight";

import express, { Handler } from "express";
import {
  Application,
  Request,
  Response,
  RequestHandler as ExpressRequestHandler,
} from "express";

const app: Application = express();

import { expressjwt as jwt } from "express-jwt";
import { logger, cors, noCache, requireAdmin } from "./middleware";
import { addDays, differenceInCalendarDays, differenceInDays, formatISO, intervalToDuration, parseISO } from "date-fns";

const secret = process.env.HEALTH_APP_SECRET_KEY;

function replaceDate(key: string, value: string) {
  if (this[key] instanceof Date) {
    value = this[key].toISOString().substring(0, 10);
  }

  return value;
}

app.set("json replacer", replaceDate);
app.set("json spaces", 2);
app.use(bodyParser.json());

app.use(logger());

app.use(cors());
app.options("*", cors());

app.use(
  jwt({ secret: secret, algorithms: ["HS256"], credentialsRequired: false })
);
app.use(requireAdmin());
app.use(noCache());

interface TypedRequestBody<T> extends Request {
  body: T;
}

type RequestHandler<T> = (
  req: TypedRequestBody<T>,
  res: Response
) => Promise<void>;

function handleError<T>(func: RequestHandler<T>): RequestHandler<T> {
  return async (req, res) => {
    try {
      await func(req, res);
    } catch (e) {
      console.log(`Error occurred`, e);
      res.sendStatus(500);
    }
  };
}

app.get(
  "/weight/log/",
  handleError(async (req, res) => {
    const data = await getAllWeight();

    res.json(data);
  })
);

function average(arr: (number | null | undefined)[]) {
  let sum = 0;
  let count = 0;

  for (let val of arr) {
    if (val !== null && val !== undefined) {
      sum += val;
      count++;
    }
  }

  return sum / count;
}

function roundTo(num: number, places: number) {
  let placesRounded = Math.round(places);
  let placesMult = Math.pow(10, placesRounded);

  return Math.round(num * placesMult + Number.EPSILON) / placesMult;
}

function createPredictor(data: WeightEntry[]) {
  const onlyFullEntries = data.filter(e => e.weightAm !== undefined && e.weightPm !== undefined);
  const stats = onlyFullEntries
    .map(e => e.weightPm - e.weightAm)
    .reduce(({ count, sum }, diff) => ({
      count: count + 1,
      sum: sum + (isNaN(diff) ? 0 : diff )
    }), { count: 0, sum: 0 })
  
  const averageDiff = roundTo(stats.sum / stats.count, 1);

  return (actualAm: number | undefined, actualPm: number | undefined) => {
    if (actualAm === undefined && actualPm === undefined) {
      return undefined;
    }

    const am = actualAm === undefined ? actualPm - averageDiff : actualAm;
    const pm = actualPm === undefined ? actualAm + averageDiff : actualPm;

    return average([am, pm]);
  }
}

type ExtendedWeightEntry = {
  date: string,
  am?: number,
  pm?: number,
  average?: number,
  presumed?: number
}

type ResultForPeriod = {

}

app.get(
  "/weight",
  handleError(async (req, res) => {
    const oneDayInMs = 1000 * 60 * 60 * 24;
    const periodQuery: string | undefined = <string | undefined>(
      req.query.period
    );
    const period = periodQuery === undefined ? 1 : parseInt(periodQuery);

    const data = await getAllWeight();

    const predictor = createPredictor(data);

    const values: ExtendedWeightEntry[] = data.map((d) => ({
      date: d.date,
      am: d.weightAm,
      pm: d.weightPm,
      average: average([d.weightAm, d.weightPm]),
      presumed: predictor(d.weightAm, d.weightPm)
    }));

    const valuesWithMissing = predictMissing(values);

    const byPeriod: Record<
      string,
      {
        sum: number;
        count: number;
        sumPresumed: number;
        countPresumed: number;
        sumAm: number;
        countAm: number;
        sumPm: number;
        countPm: number;
      }
    > = {};

    for (let { date, am, pm, average, presumed } of valuesWithMissing) {
      const daysSinceEpoch = Math.trunc(Date.parse(date) / oneDayInMs);
      const daysOffset = daysSinceEpoch % period;

      const startOfPeriod = new Date((daysSinceEpoch - daysOffset) * oneDayInMs)
        .toISOString()
        .substring(0, 10);

      if (!byPeriod[startOfPeriod]) {
        byPeriod[startOfPeriod] = {
          sum: 0,
          count: 0,
          sumPresumed: 0,
          countPresumed: 0,
          sumAm: 0,
          countAm: 0,
          sumPm: 0,
          countPm: 0,
        };
      }

      if (!isNaN(average)) {
        byPeriod[startOfPeriod].sum += average;
        byPeriod[startOfPeriod].count++;
      }

      if (!isNaN(presumed)) {
        byPeriod[startOfPeriod].sumPresumed += presumed;
        byPeriod[startOfPeriod].countPresumed++;
      }

      if (!isNaN(am)) {
        byPeriod[startOfPeriod].sumAm += am;
        byPeriod[startOfPeriod].countAm++;
      }

      if (!isNaN(pm)) {
        byPeriod[startOfPeriod].sumPm += pm;
        byPeriod[startOfPeriod].countPm++;
      }
    }

    const results = [];

    for (let [startOfPeriod, stats] of Object.entries(byPeriod)) {
      results.push({
        start: startOfPeriod,
        average: stats.sum / stats.count,
        count: stats.count,
        averagePresumed: stats.sumPresumed / stats.countPresumed,
        countPresumed: stats.countPresumed,
        averageAm: stats.countAm > 0 ? stats.sumAm / stats.countAm : null,
        countAm: stats.countAm,
        averagePm: stats.countPm > 0 ? stats.sumPm / stats.countPm : null,
        countPm: stats.countPm,
      });
    }

    res.json(results);
  })
);

app.put(
  "/weight/log/:date/:period",
  handleError<{
    weight: string;
  }>(async (req, res) => {
    const date: string = req.params.date;
    const dateValue = Date.parse(date);
    const period: string = req.params.period.toLowerCase();
    const weight: string = req.body.weight !== "" ? req.body.weight : null;

    if (period !== "am" && period !== "pm") {
      throw '"period" must either be "AM" or "PM"';
    }

    if (isNaN(dateValue)) {
      throw '"date" was invalid';
    }

    const data = await saveWeightForPeriod({
      date,
      period,
      weight,
    });

    res.json(data);
  })
);

app.post(
  "/weight/log/",
  jwt({ secret: secret, algorithms: ["HS256"] }),
  handleError<WeightEntry[]>(async (req, res) => {
    const log = req.body;

    const requests: DatePutRequest[] = [];

    for (const { date, weightAm, weightPm } of log) {
      if (weightAm) {
        requests.push({
          date,
          period: "am",
          weight: weightAm.toString(),
        });
      }
      if (weightPm) {
        requests.push({
          date,
          period: "pm",
          weight: weightPm.toString(),
        });
      }
    }

    const data = await saveAllWeights(requests);

    res.json(data);
  })
);

function predictMissing(data: ExtendedWeightEntry[]): ExtendedWeightEntry[] {
  const withMissing: ExtendedWeightEntry[] = [];
  for (let i = 0; i < data.length - 1; i++) {
    const currentEntry = data[i];
    const nextEntry = data[i + 1];
    
    const currentDate = parseISO(currentEntry.date);
    const nextDate = parseISO(nextEntry.date);

    const startValue = currentEntry.presumed;
    const endValue = nextEntry.presumed;

    const daysBetween = differenceInCalendarDays(
      nextDate,
      currentDate
    );

    const averageDailyChange = (endValue - startValue) / daysBetween;

    withMissing.push(currentEntry);


    const daysToAdd = daysBetween - 1;

    for (let j = 0; j < daysToAdd; j++) {
      withMissing.push({
        date: formatISO(addDays(currentDate, j + 1), { representation: "date" }),
        presumed: startValue + averageDailyChange * j
      });
    }
  }

  withMissing.push(data[data.length - 1]);

  return withMissing;
}

export default app;

