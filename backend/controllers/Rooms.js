
var {users, rooms} = require("../vars")
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
    if(room.includes("await")) return { error: 'Nome Indisponivel' };
    
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
  const getRooms = () => {
    return rooms;
  }
module.exports = {addRoom, inRoom, leaveRoom, getRooms}