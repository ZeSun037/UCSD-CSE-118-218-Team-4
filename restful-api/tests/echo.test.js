const request = require('supertest');
const app = require('../app');

describe('POST /echo/new', () => {
    it('should return a 200 status and a message', async () => {
        todos = {} // FIXME: adapt to TODO format
        const response = await request(app).post('/echo/new').send(todos);
        expect(response.status).toBe(200);
        expect(response.body).toEqual({ message: 'Successfully created new TODOs.' });
    });
});


describe('GET /echo/completed', () => {
    it('should return a 200 status and a message', async () => {
        const response = await request(app).get('/echo/completed').b;
        expect(response.status).toBe(200);
        expect(response.body).toEqual({}); // FIXME: adapt to the actual response
    });
});
