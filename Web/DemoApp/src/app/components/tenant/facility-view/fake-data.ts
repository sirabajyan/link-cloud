export interface Report {
  periodStart: string;
  periodEnd: string;
  measures: [{
    id: string;
    name: string;
    short: string;
  }];
  censusCount: number;
  ipCount: number;
  status: 'pending' | 'complete' | 'submitted' | 'failure';
}

export function createFakeData(): Report[] {
  return [{
    periodStart: '2020-01-01',
    periodEnd: '2020-01-31',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 100,
    ipCount: 50,
    status: 'pending'
  }, {
    periodStart: '2020-02-01',
    periodEnd: '2020-02-29',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 110,
    ipCount: 60,
    status: 'complete'
  }, {
    periodStart: '2020-03-01',
    periodEnd: '2020-03-31',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 120,
    ipCount: 70,
    status: 'submitted'
  }, {
    periodStart: '2020-04-01',
    periodEnd: '2020-04-30',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 130,
    ipCount: 80,
    status: 'failure'
  }, {
    periodStart: '2020-05-01',
    periodEnd: '2020-05-31',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 140,
    ipCount: 90,
    status: 'pending'
  }, {
    periodStart: '2020-06-01',
    periodEnd: '2020-06-30',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 150,
    ipCount: 100,
    status: 'complete'
  }, {
    periodStart: '2020-07-01',
    periodEnd: '2020-07-31',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 160,
    ipCount: 110,
    status: 'submitted'
  }, {
    periodStart: '2020-08-01',
    periodEnd: '2020-08-31',
    measures: [{
      id: '1',
      name: 'Measure 1',
      short: 'M1'
    }],
    censusCount: 170,
    ipCount: 120,
    status: 'failure'
  }];
}
