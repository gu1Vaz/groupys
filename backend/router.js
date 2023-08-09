const express = require("express");
const router = express.Router();
const path = require('path');
const {getRooms} = require('./controllers/Rooms');
const {version} = require('./vars')

router.get("/getRooms", (req, res) => {
  res.send(
    getRooms()
  ).status(200);
});
router.get("/version", (req, res) => {
  res.send(
    [version, "by None Gui"]
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