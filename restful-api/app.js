const express = require('express');
const echoRouter = require('./routers/echoRouter')
const watchRouter = require('./routers/watchRouter')

const app = express();

app.use(express.json()); 
app.use(express.urlencoded({extended: false}));

//TODO: Implement Get function
app.get('/', (req, res) => {
  res.send('Hello World');  
});

app.post('/', (req, res) => {
  console.log(req.body);
  res.status(200).send('POST request to the homepage');
})

app.use('/echo', echoRouter);

app.use('/watch', watchRouter);

const port = 3000;

app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});