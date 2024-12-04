const {Schema} = require('redis-om');

const TODO = new Schema('TODO', {
    assignee: { type: 'string' },
    place: { type: 'number' },
    time: { type: 'number' },
    priority: { type: 'number' },
    title: { type: 'string' },
    isDone: { type: 'boolean' }
});

module.exports = TODO;