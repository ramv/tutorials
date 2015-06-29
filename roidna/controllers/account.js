var Account = require('../models/account');
var crypto = require('crypto');


var generateResetToken = function(){
  crypto.randomBytes(20, function(err, buffer) {
      var token = buffer.toString('hex');
      done(err, token);
  });
}
  

module.exports = {
  generateResetToken: generateResetToken,
};