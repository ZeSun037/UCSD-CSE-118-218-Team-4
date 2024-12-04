var redis = require('redis');
const {Repository} = require('redis-om');
const TODO = require('./initTODO');
const config = require('../config.json');

class Redis {
    constructor() {
        this.host = config['redis-socket']['host'];
        this.port = config['redis-socket']['port'];
        this.connected = false;
        this.client = null;
        this.todoRepository = null;
    }

    async getConnection() {

        var obj = this;

        if(obj.connected) {
            console.log("returning Redis client.");
            return obj.client;
        }
        else {
            obj.client =  redis.createClient({
                password: config['redis-key'],
                socket: {
                    host: config['redis-socket']['host'],
                    port: config['redis-socket']['port']
                }
            });

            obj.client.on('connect', function() {
                console.log('Redis Connecting!');
            });

            obj.client.on('ready', function() {
                console.log('Redis Ready!');
                obj.connected = true;
            });

            obj.client.on( 'error', function () {
                console.log('Error: redis disconnected!');
                obj.connected = false;
            });

            obj.client.on( 'end', function () {
                console.log('End: redis connection ended!');
                obj.connected = false;
            });

            try{
                console.log("connecting new redis client!");
                await obj.client.connect();
                this.todoRepository = new Repository(TODO, obj.client);
                await this.todoRepository.createIndex();
                obj.connected = true;
                console.log("connected to new redis client!");
            }
            catch(e){
                console.log("redis connect exception caught: " + e);
                return null;
            }
            
            return obj.client;
        }
    }
}

// This will be a singleton class. After first connection npm will cache this object for whole runtime.
// Every time you will call this getConnection() you will get the same connection back
module.exports = new Redis();