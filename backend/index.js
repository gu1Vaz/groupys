const http = require('http');
const express = require('express');
const socketio = require('socket.io');
const cors = require('cors');
const router = require('./router');

const app = express();
const server = http.createServer(app);
const io = socketio(server, {allowEIO3: true});

const {addRoom, inRoom, leaveRoom, getSalas} = require('./usersAndrooms');
const {addUser, removeUser, setNameUser, getUser, getUsers, getUsersInRoom} = require('./usersAndrooms');
const {joinMatch, findMatch, leaveMatch, getUserMatch, getUsersMatch} = require('./usersAndrooms');

var corsOptions = {
  origin: 'http://hackin.online:3000',
  optionsSuccessStatus: 200 
}
app.use(cors(corsOptions));
app.use(router);

app.use((err, req, res, next)=>{
  console.error("ERRO FATAL",err);
});


io.on('connection',(socket)=>{
  //User
  const { error, user } = addUser({ id: socket.id });
  
  socket.on("set_name",(name,callback)=>{
    const { error, success, success_guest } = setNameUser({ id: socket.id, name });
    if(error) return callback(["error",error]);
    if(success) return callback(["success",success]);
    if(success_guest) return callback(["success_guest",success_guest]); 
  });
  socket.on('disconnect', () => {
    const user = removeUser(socket.id);
    leaveMatch({id:socket.id});
    if(user) {
      io.to(user.room).emit('message', { user: 'admin', text: `${user.name} saiu.` });
      io.to(user.room).emit('roomData', { room: user.room, users: getUsersInRoom(user.room)});
      socket.broadcast.emit('listData', {user: 'admin', rooms: getSalas() }); 
    }
  });
  
  //Salas
  socket.on("join",(room,callback)=>{
    const { error, user } = inRoom({ id: socket.id, room });

    if(error) return callback(["error",error]);

    socket.join(user.room);

    //socket.emit('message', { user: 'admin', text: `${user.name}, welcome to room ${user.room}.`});
    socket.broadcast.to(user.room).emit('message', {user: 'admin', text: `${user.name} has joined!` });
    socket.broadcast.emit('listData', {user: 'admin', rooms: getSalas() });
    io.to(user.room).emit('roomData', { room: user.room, users: getUsersInRoom(user.room) });
    
    return callback( [ "success", getUsersInRoom(user.room)])
  });
  socket.on("create",(room,max,callback)=>{
    const { error, sala } = addRoom({id:socket.id,room,max });

    if(error) return callback(["error",error]);

    socket.join(sala.nome);
    socket.emit('message', { user: 'admin', text: `Sua sala foi criada, bem vindo a ${sala.nome}.`});
    socket.broadcast.emit('listData', {user: 'admin', rooms: getSalas() });
    io.to(sala.nome).emit('roomData', { room: sala.nome, users: getUsersInRoom(sala.nome) });
    
    return callback( [ "success", getUsersInRoom(sala.nome)])
  });
  socket.on("leave",(callback)=>{
    const { error, user, sala} = leaveRoom({ id: socket.id });

    if(error) return callback(error);
    socket.leave(sala);
    
    io.to(sala).emit('message', { user: 'admin', text: `${user.name} saiu.` });
    io.to(sala).emit('roomData', { room: sala, users: getUsersInRoom(sala)});
    socket.broadcast.emit('listData', {user: 'admin', rooms: getSalas() });
  });

  //Salas Privadas
  socket.on("join_match",()=>{
    joinMatch({ id: socket.id });
  });
  socket.on("find_match",(callback)=>{
    const {match, error} = findMatch({ id: socket.id });

    if(error) return callback(["error",error]);

    io.to(match).emit('matchFound');
    return callback(["success"]);
  });
  socket.on("leave_match",()=>{
    const user = leaveMatch({ id: socket.id });
    io.to(user.room).emit('message', { user: 'admin', text: `${user.name} saiu.` });
  });

  //Messages
  socket.on('sendMessage', (message) => {
    const user = getUser(socket.id);
    if(user) io.to(user.room).emit('message', { user: user.name, text: message });
  });
  socket.on('sendMessageMatch', (message) => {
    const user = getUserMatch(socket.id);
    if(user) io.to(user.room).emit('message', { user: user.name, text: message });
  });
  
});




server.listen( 3000, () => console.log(`Server has started.`));