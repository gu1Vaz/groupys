const rand = (min, max, excludeArray) => {
    const randomNumber = Math.floor(Math.random() * (max - min + 1 - excludeArray.length)) + min;
    return randomNumber + excludeArray.sort((a, b) => a - b).reduce((acc, element) => { return randomNumber >= element - acc ? acc + 1 : acc}, 0);
}
module.exports = {rand};


  
