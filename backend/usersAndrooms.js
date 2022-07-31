const {rand} = require("./random.js")

const users = [];
const pv_users = [];
const rooms = [{count:0, max:10, nome:"global 1"},{count:0, max:10, nome:"global 2"},{count:0, max:10, nome:"global 3"}];
var guest_count = 0;

//Salas
const addRoom = ({id, room, max }) => {
  //dados
  if(!id) return { error: 'Fatal Error' };
  if(!room || room.length>22) return { error: 'Nome da Sala > 22 ou em falta' };
  if(!max || max>7) return { error: 'Qtd de pessoas > 7 ou em falta' };
  if(room.includes("pv_")) return { error: 'Nome Indisponivel' };

  const nameSala = room.trim().toLowerCase();

  const user = users.find((user) => user.id === id);
  const existingRoom = rooms.find(obj => obj.nome == nameSala );
  const index = users.findIndex((user) => user.id === id);

  //user
  if(!user) return {error:'Você não esta no servidor,recarregue a pagina'};
  if(user.room != null) return {error:'Já esta em uma sala'};
  //room
  if(existingRoom) return { error: 'Sala ja existe' };

  const sala = { count:1, max, nome:nameSala};

  rooms.push(sala);
  users[index].room = sala.nome;

  return { sala };
}
const inRoom = ({id,room }) => {
  //dados
  if(!id) return { error: 'Fatal Error' };
  if(!room) return { error: 'Nome da Sala é requirido' };
  if(room.includes("pv_")) return { error: 'Nome Indisponivel' };
  
  nameSala = room.trim().toLowerCase();

  const user = users.find((user) => user.id === id);
  const index = users.findIndex((user) => user.id === id);
  const existingRoom = rooms.find(obj => obj.nome == nameSala );
  const sala = rooms.find(obj => obj.nome == nameSala);
  const index2 = rooms.findIndex(obj => obj.nome == nameSala);

  //user
  if(!user) return {error:'Você não esta no servidor,recarregue a pagina'};
  if(user.room != null) return {error:'Já esta em uma sala'};
  //room
  if(!existingRoom) return { error: 'Sala nao existe' };
  if(sala.count == sala.max) return { error: 'Sala esta cheia' };

  
  rooms[index2].count+=1;
  users[index].room = room;

  return{user};
  
}
const leaveRoom = ({id }) => {
  if(!id) return { error: 'Fatal Error' };
  const user = users.find((user) => user.id === id);
  const index = users.findIndex((user) => user.id === id);
  //user
  if(!user) return {error:'Você não esta no servidor,recarregue a pagina'};
  if(user.room == null) return {error:'Não esta em nenhuma sala'};
  
  const index2 = rooms.findIndex(obj => obj.nome == user.room);

  rooms[index2].count -= 1;
  if(rooms[index2].count === 0 && rooms[index2].max<10){rooms.splice(index2, 1)[0]}
  
  const sala = user.room;
  users[index].room = null;
  return {user,sala};
}
const getSalas = () => {
  return rooms;
}

//Users
const addUser = ({ id }) => {
  if(!id) return { error: 'Fatal Error' };

  const existingUser = users.find((user) => user.id === id);

  //user
  if(existingUser) return { error: '' };

  const user = { id, name:null,room:null};

  users.push(user);
  return { user };
}

const removeUser = (id) => {
  if(!id) return { error: 'Fatal Error' };
  const index = users.findIndex((user) => user.id === id);

  if(index !== -1) {
    const userRemoved = users.splice(index, 1)[0];
    if(userRemoved.room != null){
      const usersInSala = users.find(user => user.room == userRemoved.room);
      const index = rooms.findIndex((room) => room.nome === userRemoved.room); 
      if (!usersInSala && rooms[index].max<10) {
        rooms.pop(index,1);
      }else{
        rooms[index].count -=1;
      };
    }
    return userRemoved
  };
}
const setNameUser = ({ id, name }) => {
  //dados
  if(!id) return { error: 'Fatal Error' };
  let isGuest =false;
  if(!name){ 
    name = "guest_"+guest_count;
    guest_count++;
    isGuest = true;
  }
  name = name.trim().toLowerCase();

  const index = users.findIndex((user) => user.id === id);
  
  //user
  if(name == "admin" || name == "Admin" || name.startsWith("admin") || name.endsWith("admin") ) return {error:"Você não é admin"};
  if(index === -1) return { error: 'Você não esta no servidor,recarregue o app' };
  if(users[index].room !== null) return { error: 'Não é possivel definir nome dentro de uma sala' };

  users[index].name = name;

  if(isGuest) return { success_guest:name }
  return { success:name };
}
const getUser = (id) => users.find((user) => user.id === id);
const getUsers = () => { return users;}
const getUsersInRoom = (room) => users.filter((user) => user.room === room);

//Salas Privadas
const joinMatch = ({ id }) => {
  if(!id) return { error: 'Fatal Error' };
  const existingUser = pv_users.find((user) => user.id === id);

  //user
  if(existingUser) return { error: '' };

  const user = { id, name:"Anônimo",room:null};

  pv_users.push(user);
  return { user };
}
const findMatch = ({ id }) => {
  if(!id) return { error: 'Fatal Error' };
  const index = pv_users.findIndex((user) => user.id === id);
  const excludes  = pv_users.map((e, i) => e.room != null ? i : "").filter(String);
  if(pv_users.length <= 1) return {error:"Sem pessoas no momento"}
  if(pv_users[index] == -1) return {error:"Erro Fatal"}
  if(pv_users[index].room) return {error:"Ja esta em uma sala"}

  excludes.push(index);

  const match = rand(0, pv_users.length-1, excludes);
  if(match === pv_users.length) return {error:"Sem pessoas no momento"}
  
  try{
    pv_users[match].room = id;
    pv_users[index].room = pv_users[match].id;
    return {match:pv_users[match].id}
  }catch(e){
    return {error:404}
  }
}
const leaveMatch = ({ id }) => {
  if(!id) return { error: 'Fatal Error' };
  const index = pv_users.findIndex((user) => user.id === id);

  if(index !== -1) {
    const userRemoved = pv_users.splice(index, 1)[0];
    if(userRemoved.room){
      const index2 = pv_users.findIndex((user) => user.id === userRemoved.room);
      if(index2 != -1) pv_users[index2].room = null;
    }
    return userRemoved;
  };

}
const getUserMatch = (id) => pv_users.find((user) => user.id === id);
const getUsersMatch = () => { return pv_users;}

module.exports = {addRoom, inRoom, leaveRoom, getSalas,
                  addUser, removeUser, setNameUser, getUser, getUsers, getUsersInRoom,
                  joinMatch, findMatch, leaveMatch, getUserMatch, getUsersMatch};