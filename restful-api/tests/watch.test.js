const request = require('supertest');
const app = require('../app');

describe('GET /watch/nikolas', () => {
    it('should return a 200 status and a message', async () => {
        const response = await request(app).get('/watch/nikolas');
        expect(response.status).toBe(200);
        expect(response.body).toEqual({}); // FIXME: adapt to the actual response
    });
});

describe('GET /watch/nonexistent', () => {
    it('should return a 404 for an unknown user', async () => {
        const response = await request(app).get('/watch/nonexistent');
        expect(response.status).toBe(404);
        expect(response.body).toEqual({ message: 'The user does not exist!' });
    });
});


describe('DELETE /watch/nikolas', () => {
    it('should return a 200 status and a message', async () => {
        tasks = {} // FIXME: adapt to the actual response
        const response = await request(app).delete('/watch/nikolas').send(tasks);
        expect(response.status).toBe(200);
        expect(response.body).toEqual({ message: 'Your tasks are registered to be completed.' });
    });
});

describe('DELETE /watch/nonexistent', () => {
    it('should return a 404 for an unknown user', async () => {
        const response = await request(app).delete('/watch/nonexistent');
        expect(response.status).toBe(404);
        expect(response.body).toEqual({ message: 'The user does not exist!' });
    });
});
