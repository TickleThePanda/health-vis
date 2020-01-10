const bodyParser = require('body-parser');
const cors = require('cors');

const weightRepo = require('./repo/weight.js');

const express = require('express');
const app = express();

const jwt = require('express-jwt');

const secret = process.env.HEALTH_APP_SECRET_KEY;

const port = process.env.PORT || 8080;

const whitelist = [
  /^https:\/\/((\w|-)*\.)*ticklethepanda\.(co\.uk|dev|netlify\.com)$/
];

const corsOptions = {
  origin: function(origin, callback) {
    if (!origin) {
      callback(null, true);
    } else if (whitelist.some(r => origin.match(r))) {
      callback(null, true);
    } else {
      callback(new Error('Not allowed by CORS'));
    }
  },
  credentials: true
}

const replaceDate = function replaceDate(key, value) {

  if(this[key] instanceof Date) {
    value = this[key].toISOString().substring(0, 10);
  }
  
  return value;
}

app.set('json replacer', replaceDate);
app.set('json spaces', 2);
app.use(bodyParser.json());

app.use(cors(corsOptions));

app.options('*', cors(corsOptions));

app.get('/weight/log/', (req, res) => {
  weightRepo.getAllWeight()
    .then(data => {
      res.json(data);
    });
});

function averageExcludingNulls(arr) {
  let sum = 0;
  let count = 0;
  
  for (let val of arr) {
    if (val !== null) {
      sum += val;
      count++;
    }
  }
  
  return sum / count;

}

app.get('/weight', (req, res) => {

  const oneDayInMs = 1000 * 60 * 60 * 24;
  const period = req.query.period;

  weightRepo.getAllWeight()
    .then(data => {
      const values = data
        .map(d => ({
          date: d.date,
          average: averageExcludingNulls([d.weightAm, d.weightPm]),
        }));
      
      const inPeriod = {};

      for (let value of values) {
        const date = value.date;
        const weightAverage = value.average;

        const daysSinceEpoch = Math.trunc(Date.parse(date) / oneDayInMs);
        const daysOffset = daysSinceEpoch % period;

        const startOfPeriod = new Date((daysSinceEpoch - daysOffset) * oneDayInMs)
                .toISOString().substring(0, 10);

        if(!inPeriod[startOfPeriod]) {
          inPeriod[startOfPeriod] = {
            sum: 0,
            count: 0
          };
        }

        inPeriod[startOfPeriod].sum += weightAverage;
        inPeriod[startOfPeriod].count++;
      }

      const results = [];

      for (let entry of Object.entries(inPeriod)) {
        const startOfPeriod = entry[0];
        const sum = entry[1].sum;
        const count = entry[1].count;

        results.push({
          start: startOfPeriod,
          average: sum / count,
          count: count
        });
      }

      return results;
    })
    .then(data => {
      res.json(data);
    });
})

app.put('/weight/log/:date/:meridiam', jwt({ secret: secret }), (req, res) => {

  if (!req.user.roles.includes('admin')) {
    res.send(401);
    return;
  }

  const dateString = req.params.date;
  const dateValue = Date.parse(dateString);
  const meridiam = req.params.meridiam;
  const weight = req.body.weight !== "" ? req.body.weight : null;

  if (meridiam !== 'AM' && meridiam !== 'PM') {
    throw '"meridiam" must either be "AM" or "PM"';
  }

  if (isNaN(dateValue)) {
    throw '"date" was invalid';
  }

  weightRepo.saveWeightForMeridiam(dateString, meridiam, weight)
    .then(data => res.json(data));

});

app.listen(port, () =>
  console.log(`App started on ${port}`)
);

