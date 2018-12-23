const pg = require('pg-promise')();
const db = pg(process.env.DATABASE_URL);

async function getAllWeight() {
  const data = await db.manyOrNone('SELECT * FROM weight ORDER BY date ASC');
  return data
    .map(d => ({
      id: d.weight_id,
      date: d.date,
      weightAm: d.weight_am,
      weightPm: d.weight_pm
    }))
    .filter(d => d.weightAm || d.weightPm);  
}

async function saveWeightForMeridiam(dateString, meridiam, weight) {
  const dateValue = Date.parse(date);

  const data = await db.oneOrNone('SELECT * FROM weight WHERE date = TO_TIMESTAMP(${date} / 1000)::date', {
      date: dateValue
    })

  const field = "weight_" + meridiam.toLowerCase();

  if (data) {
    const query = 'UPDATE weight ' 
              +   '   SET ' + field + ' = ${weight}'
              +   ' WHERE weight_id = ${weight_id}';
    await db.none(query, {
      weight: weight,
      weight_id: data.weight_id
    });

  } else {
    const query = 'INSERT INTO weight (weight_id, date, ' + field + ')'
              +   'VALUES (DEFAULT, TO_TIMESTAMP(${date} / 1000)::date, ${weight})';
    await db.none(query, {
      date: dateValue,
      weight: weight
    });
  }
    
  return {
    date: dateString,
    meridiam: meridiam,
    weight: weight
  };

}

module.exports = {
  getAllWeight: getAllWeight
};

