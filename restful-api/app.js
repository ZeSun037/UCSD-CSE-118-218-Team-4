const express = require("express");

const app = express();

//TODO: Implement Get function
app.get('/', (req, res) => {
    res.send('Hello World');  
  });

//TODO: Implement Post function
app.post('/submit-item', (req, res) => {
res.send('TODO item submitted');
});

const port = 3000;

app.listen(port, () => {
  console.log(`Server listening on port ${port}`);  
});