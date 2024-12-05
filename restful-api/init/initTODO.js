const {Schema} = require('redis-om');

const TODO = new Schema('TODO', {
    assignee: { type: 'string' },
    place: { type: 'string' },
    time: { type: 'string' },
    priority: { type: 'string' },
    title: { type: 'string' },
    isDone: { type: 'boolean' }
});

module.exports = TODO;