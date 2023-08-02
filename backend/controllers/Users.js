var {users, rooms, guest_count} = require("../vars")
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



module.exports = {addUser, removeUser, setNameUser, getUser, getUsers, getUsersInRoom,};