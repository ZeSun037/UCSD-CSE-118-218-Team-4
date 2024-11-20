//const { createClient } = require("redis");
const Ably = require('ably');
const config = require('../config.json');

// const redisClient = () => {
//     const redisInstance = createClient({
//         password: config['redis-key'],
//         socket: {
//             host: config['redis-socket']['host'],
//             port: config['redis-socket']['port']
//         }
//     });

//     redisInstance.on('error', (error) => {
//         if (error) {
//             console.log(error);
//         } else {
//             console.log("Successfully connected to Redis Server.")
//         }
//     })

//     redisInstance.on('ready', (res) => {
//         console.log("Redis successfully connected.");
//     })

//     return redisInstance;
// }

// const redisConnect = async (redisInstance) => {
//     await redisInstance.connect();
// }

const ablyClient = () => {
    const ablyRealtime = new Ably.Realtime(config['ably-key']);
    return ablyRealtime;
}

module.exports = {ablyClient};
