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

app.get(
  "/weight",
  handleError(async (req, res) => {
    const oneDayInMs = 1000 * 60 * 60 * 24;
    const periodQuery: string | undefined = <string | undefined>(
      req.query.period
    );
    const period = periodQuery === undefined ? 1 : parseInt(periodQuery);

    const data = await getAllWeight();

    const values = data.map((d) => ({
      date: d.date,
      am: d.weightAm,
      pm: d.weightPm,
      average: average([d.weightAm, d.weightPm]),
    }));

    const inPeriod: Record<
      string,
      {
        sum: number;
        count: number;
        sumAm: number;
        countAm: number;
        sumPm: number;
        countPm: number;
      }
    > = {};

    for (let { date, am, pm, average } of values) {
      const daysSinceEpoch = Math.trunc(Date.parse(date) / oneDayInMs);
      const daysOffset = daysSinceEpoch % period;

      const startOfPeriod = new Date((daysSinceEpoch - daysOffset) * oneDayInMs)
        .toISOString()
        .substring(0, 10);

      if (!inPeriod[startOfPeriod]) {
        inPeriod[startOfPeriod] = {
          sum: 0,
          count: 0,
          sumAm: 0,
          countAm: 0,
          sumPm: 0,
          countPm: 0,
        };
      }

      inPeriod[startOfPeriod].sum += average;
      inPeriod[startOfPeriod].count++;

      if (am !== null && am !== undefined) {
        inPeriod[startOfPeriod].sumAm += am;
        inPeriod[startOfPeriod].countAm++;
      }

      if (pm !== null && pm !== undefined) {
        inPeriod[startOfPeriod].sumPm += pm;
        inPeriod[startOfPeriod].countPm++;
      }
    }

    const results = [];

    for (let [startOfPeriod, stats] of Object.entries(inPeriod)) {
      results.push({
        start: startOfPeriod,
        average: stats.sum / stats.count,
        count: stats.count,
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

export default app;
