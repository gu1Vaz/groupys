const {rand} = require("../scripts/random.js")
var {pv_users} = require("../vars")
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
    console.log(pv_users)
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
        if(index2 != -1) pv_users[index2].room = "await";
      }
      return userRemoved;
    };
  
  }
  const getUserMatch = (id) => pv_users.find((user) => user.id === id);
  const getUsersMatch = () => { return pv_users;}

  module.exports = { joinMatch, findMatch, leaveMatch, getUserMatch, getUsersMatch } 