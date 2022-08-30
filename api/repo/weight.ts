import {
  AttributeValue,
  BatchWriteItemCommand,
  DynamoDBClient,
  ExecuteStatementCommand,
  WriteRequest,
} from "@aws-sdk/client-dynamodb";

const DYNAMO_DB_REGION = "us-east-1";

const client = new DynamoDBClient({
  region: DYNAMO_DB_REGION,
  credentials: {
    accessKeyId: process.env.HEALTH_VIS_AWS_ACCESS_KEY_ID,
    secretAccessKey: process.env.HEALTH_VIS_AWS_SECRET_ACCESS_KEY,
  },
});

export type WeightEntry = {
  date: string;
  weightAm: number | undefined;
  weightPm: number | undefined;
};

type WeightDbEntry = {
  YearMonth: AttributeValue.SMember;
  DayPeriod: AttributeValue.SMember;
  Weight: AttributeValue.SMember | AttributeValue.NULLMember;
};

export async function getAllWeight(): Promise<WeightEntry[]> {
  const command = new ExecuteStatementCommand({
    Statement: `
    SELECT *
      FROM Weight
    `,
  });

  const { Items: items } = await client.send(command);

  const results: Record<string, Record<string, number>> = {};

  for (const item of items) {
    const [day, period] = item.DayPeriod.S.split("#");
    const date = item.YearMonth.S + "-" + day;
    if (results[date] === undefined) {
      results[date] = {};
    }

    results[date][period] = parseFloat(item.Weight.S);
  }

  return Object.entries(results)
    .map(([date, { am, pm }]) => ({
      date: date,
      weightAm: am,
      weightPm: pm,
    }))
    .filter((d) => d.weightAm || d.weightPm)
    .sort((a, b) => a.date.localeCompare(b.date));
}

export async function saveWeightForPeriod(request: DatePutRequest) {
  await saveAllWeights([request]);

  return request;
}

function* chunks<T>(arr: T[], n: number): Generator<T[]> {
  for (let i = 0; i < arr.length; i += n) {
    yield arr.slice(i, i + n);
  }
}

export async function saveAllWeights(requests: DatePutRequest[]) {
  const requestChunks: WriteRequest[][] = [
    ...Array.from(
      chunks(
        requests.map(toDbEntry).map((e) => ({
          PutRequest: {
            Item: e,
          },
        })),
        25
      )
    ),
  ];

  await Promise.all(
    requestChunks.map((requests) =>
      client.send(
        new BatchWriteItemCommand({
          RequestItems: {
            Weight: requests,
          },
        })
      )
    )
  );

  return requests;
}

function toDbEntry({ date, period, weight }: DatePutRequest): WeightDbEntry {
  const [year, month, day] = date.split("-");
  const yearMonth = `${year}-${month}`;
  const dayPeriod = `${day}#${period.toLowerCase()}`;

  const item: WeightDbEntry = {
    YearMonth: { S: yearMonth },
    DayPeriod: { S: dayPeriod },
    Weight:
      weight === null
        ? {
            NULL: true,
          }
        : {
            S: weight,
          },
  };

  return item;
}

export type DatePutRequest = {
  date: string;
  period: "am" | "pm";
  weight: string | null;
};
