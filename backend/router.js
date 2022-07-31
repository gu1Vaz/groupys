const express = require("express");
const router = express.Router();
const path = require('path');
const {getSalas,getUsers, getUsersMatch} = require('./usersAndrooms');

router.get("/getSalas", (req, res) => {
  res.send(
    getSalas()
  ).status(200);
});

// router.get("/getUsers", (req, res) => {
//   res.send(
//     getUsers()
//   ).status(200);
// });

// router.get("/teste", (req, res) => {
//   res.send(
//     getUsersMatch()
//   ).status(200);
// });


module.exports = router;