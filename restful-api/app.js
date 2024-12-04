/**
 * @file app.js
 * @description Main application file that sets up the Express application and defines routes.
 */

const express = require('express');
const echoRouter = require('./routers/echoRouter');
const watchRouter = require('./routers/watchRouter');

const app = express();

/**
 * Middleware to parse incoming JSON and URL-encoded request bodies.
 */
app.use(express.json()); 
app.use(express.urlencoded({ extended: false }));

/**
 * Root route that returns a "Hello World" response.
 * @route GET /
 * @returns {string} - "Hello World"
 */
app.get('/', (req, res) => {
  res.send('Hello World');  
});

app.post('/', (req, res) => {
  console.log(req.body);
  res.send("Boo!");
})

app.post('/', (req, res) => {
  console.log(req.body);
  res.send("Boo!");
});

/**
 * Use echoRouter to handle /echo routes.
 * @route /echo
 */
app.use('/echo', echoRouter);

/**
 * Use watchRouter to handle /watch routes.
 * @route /watch
 */
app.use('/watch', watchRouter);

const port = 3000;

/**
 * Start the server and listen on the specified port.
 * @param {number} port - The port number on which the server listens.
 */
app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});