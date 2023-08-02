const http = require('http');
const express = require('express');
const socketio = require('socket.io');
const cors = require('cors');
const router = require('./router');

const app = express();
const server = http.createServer(app);
const io = socketio(server, {allowEIO3: true});

const { addUser } = require("./controllers/Users")
const handlers = require('./handlers');


var corsOptions = {
  origin: 'https://groupys.nonegui.cloud/',
  optionsSuccessStatus: 200 
}
app.use(cors(corsOptions));
app.use(router);

app.use((err, req, res, next)=>{
  console.error("ERRO FATAL",err);
});

io.on('connection',(socket)=>{
  const { error, user } = addUser({ id: socket?.id });
  try{
    handlers(socket, io);
  }catch(e){
    if(error) return callback(["error","Fatal Error"]);
  }
  if(error) return callback(["error",error]);
});


server.listen( 3000, () => console.log(`Server has started.`));