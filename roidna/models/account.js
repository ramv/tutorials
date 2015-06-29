var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var passportLocalMongoose = require('passport-local-mongoose');

/**
 * A Validation function for local strategy password
 */
var validateLocalStrategyPassword = function(password) {
	return (this.provider !== 'local' || (password && password.length > 6));
};


var Account = new Schema({
    username: {
      type: String
    },
    password: {
      type: String,
      validate: [validateLocalStrategyPassword, 'Password should be longer']
    },
    nickname:{
      type: String
    },
    emailVerified: {
      type: Boolean,
      default: false,
    },
    emailVerifiedToken:{
      type: String
    },
    emailVerifiedTokenExpires:{
      type: String
    },
  	updated: {
		type: Date
	},
	created: {
		type: Date,
		default: Date.now
	},
	/* For reset password */
	resetPasswordToken: {
		type: String
	},
	resetPasswordTokenExpires: {
		type: Date
	}
});

Account.plugin(passportLocalMongoose, {});

module.exports = mongoose.model('Account', Account);