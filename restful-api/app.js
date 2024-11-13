const express = require("express");
const websocket = require('express-ws');
const app = express();
const socketApp = websocket(app);

//TODO: Implement Get function
app.get('/', (req, res) => {
    res.send('Hello World');  
  });

//TODO: Implement Post function
app.post('/submit-item', (req, res) => {
res.send('TODO item submitted');
});

socketApp(app);

const port = 3000;

app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});