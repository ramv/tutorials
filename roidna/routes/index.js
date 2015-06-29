var express = require('express');
var router = express.Router();
var Account = require('../models/account');
var passport = require('passport');
var render = require('../controllers/render');
var util = require('util');
var async = require('async');
var crypto = require('crypto');
var mail = require('../controllers/mail');



/* GET home page. */
router.get('/', function(req, res, next) {
  render.renderPartial(res, next,'index', { title: 'Express' });
});


router.get('/register', function(req, res, next) {
    render.renderPartial(res, next, 'register', {alert:  { style: 'hidden' }});
});

router.post('/register', function(req, res, next) {
  crypto.randomBytes(20, function(err, buffer) {
    if(err){
      console.log('POST register: ' +JSON.stringify(err));
      return render.renderPartial(res, next, 'register', { alert:  { style: '',  message: err.message} });
    }
    var token = buffer.toString('hex');
    var tokenExpiry =  Date.now() + 3600000*24*7; // 7days
    Account.register(new Account({username: req.body.username, 
                            provider: 'local', 
                            password: req.body.password,
                            emailVerifiedToken: token,
                            emailVerifiedTokenExpires: tokenExpiry,
                            nickname: req.body.nickname }), 
                  req.body.password, 
                  function(err, account) {
      if (err) {
          console.log('POST register: ' +JSON.stringify(err));
          return render.renderPartial(res, next, 'register', { alert:  { style: '',  message: err.message} });
      }
      res.render('verify_email_email', {
              name: account.nickname,
              appName: "Please verify email",
              url: 'http://' + req.headers.host + '/verify-email?token=' + token
          }, 
          function(err, emailHTML) {

        if(err){
           console.log('ERROR: router.post.register Error: '+ util.inspect(err));
          return cb(err);
        }
        mail.send(account.username, 
                  account.nickname, 
                  'admin@raptive.com', 
                  'admin@raptive.com', 
                  emailHTML, 
                  "Please Verify E-Mail address", 
                  function(err){
          return render.renderProfilePage(err, account, res, next);
        });
      });
    });
  });
});


router.get('/verify-email', function(req, res, next) {
  if(req.params.hasOwnProperty('token')){
    console.log("ERROR: GET verify-email: no token");
    return render.renderPartial(res, next, 'verify_email', {
                          message: 'Could not verify the email address',
                          alert:  { 
                            style: 'alert-error', 
                            message: err.message     
                          }
    }); 
  }
  Account.findOne({
    emailVerifiedToken: req.params.token,
  }, function(err, account) {
    // TODO check for token expiry
    if(err){
      console.log("ERROR: GET verify-email: "+JSON.stringify(err));
      return render.renderPartial(res, next, 'verify_email', {
                            message: 'Could not verify the email address',
                            alert:  { 
                              style: 'alert-error', 
                              message: err.message     
                            }
      });
    }
    if (!account) {
      console.log("ERROR: GET verify-email: Account not found");
      return render.renderPartial(res, next, 'verify_email', {
        
                            message: 'Could not verify the email address',
                            alert:  { 
                              style: 'alert-error', 
                              message: 'Could not find the user for the token'      
                            }
      });
    }
    account.emailVerified = true;
    account.save(function(err){
      if(err){
        console.log("ERROR: GET verify-email: "+JSON.stringify(err));
        return render.renderPartial(res, next, 'verify_email', {
                            message: 'Could not verify the email address',
                            alert:  { 
                              style: 'alert-error', 
                              message: err.message     
                            }
        });
      }
      render.renderPartial(res, next, 'verify_email', {
                          message: 'Successfully verified the email address',
                          alert:  { 
                            style: 'alert-success', 
                            message: 'Email Verified' 
                          }
      });
    });
  });
});
  
router.get('/login', function(req, res, next) {
  render.renderPartial(res, next, 'login', { alert: {style: 'hidden'} });
});

router.post('/login', function(req, res, next) {
  console.log('INFO: router.post: '+ util.inspect(req.body));
  passport.authenticate('local', function(err, account, info) {
    if(err){
      return render.renderPartial(res, next, 'login', { alert: {style: '', message: err.message} });
    }
    if(!account){
      return render.renderPartial(res, next, 'login', { alert: {style: '', message: "Could not find the user"} });
    }
    // Remove sensitive data before login
    account.password = undefined;
    account.salt = undefined;

    req.login(account, function(err) {
        if (err) {
            return render.renderPartial(res, next, 'login', { alert: {style: '', message: err.message} });
        } else {
            return render.renderProfilePage(err, account, res, next);
        }
    });  
  })(req, res, next);
});

router.get('/profile', function(req, res, next) {
  if(!req.isAuthenticated()){
    res.redirect('/login');
  }
  Account.findByUsername(req.session.passport.user, function(err, account){
    render.renderProfilePage(err, account, res, next);
  });
});

router.get('/reset-password', function(req, res, next){
  console.log("INFO: GET reset-password: "+JSON.stringify(req.query));
  if(req.query.hasOwnProperty('token')){
    Account.findOne({
        resetPasswordToken: req.query.token,
      }, function(err, account) {
      //TODO check for token expiry
      if(err){
        console.log("ERROR: GET reset-password: "+JSON.stringify(err));
        return render.renderPartial(res, next, 'reset_password', {
                              login: 'hidden', password: 'hidden', error: '',
                              message: 'Could not verify the reset password token',
                              alert:  { 
                                style: 'alert-error', 
                                message: err.message     
                              }
        });
      }
      if (!account) {
        console.log("ERROR: GET reset-password: Account not found");
        return render.renderPartial(res, next, 'reset_password', {
                              login: 'hidden', password: 'hidden', error: '',
                              message: 'Could not validate token',
                              alert:  { 
                                style: 'alert-error', 
                                message: 'Could not find the user for the token'      
                              }
        });
      }
      console.log("INFO: GET reset-password: Account found");
      req.session.account = account;
      render.renderPartial(res, next, 'reset_password', {login: 'hidden', password: '', error: 'hidden'});
    });
  }else{
    console.log("INFO: GET reset-password: No token show email");
    render.renderPartial(res, next, 'reset_password', {login: '', password: 'hidden', error: 'hidden'});
  }
});

router.post('/reset-password-update', function(req, res, next){
  if(!req.session.hasOwnProperty('account')){
    console.log('ERROR: router.post. reset-password-update: '+util.inspect(req.session));
    return render.renderPartial(res, 
                                next, 
                                'reset_password', 
                                { 
                                  password: 'hidden',
                                  login: 'hidden',
                                  error: '',
                                  alert: { 
                                            style: 'alert-error', 
                                            message: 'Could not find the account'
                                          } 
                                }); 
  }
  Account.findOne({
      username: req.session.account.username,
    }, function(err, account){
    if(err){
      return render.renderPartial(res, 
                              next, 
                              'reset_password', 
                              { 
                                password: 'hidden',
                                login: 'hidden',
                                error: '',
                                alert: { 
                                          style: 'alert-error', 
                                          message: err.message
                                        } 
                              }); 
    }
    if(!req.body.hasOwnProperty('password')){
      //render error
       return render.renderPartial(res, 
                          next, 
                          'reset_password', 
                          { 
                            password: 'hidden',
                            login: 'hidden',
                            error: '',
                            alert: { 
                                      style: 'alert-error', 
                                      message: 'No password sent'
                                    } 
                          }); 
    }
    account.password = req.body.password;
    account.save(function(err){
      return render.renderPartial(res, 
                    next, 
                    'reset_password', 
                    {
                      password: 'hidden',
                      login: 'hidden',
                      error: '',
                      alert: { 
                                style: 'alert-success', 
                                message: 'Successfully updated password'
                              } 
                    });
    });
  });
});
router.post('/reset-password-email', function(req, res, next){
  console.log("reset-password-email: "+JSON.stringify(req.body));

  Account.findByUsername(req.body.username, function(err, account){
    if(err){
      console.log('ERROR: router.post. reset-password: '+util.inspect(user));
      return render.renderPartial(res, 
                                  next, 
                                  'reset_password', 
                                  { 
                                    password: 'hidden',
                                    login: 'hidden',
                                    error: '',
                                    alert: { 
                                              style: 'alert-error', 
                                              message: err.message
                                            } 
                                  }); 
    }
    if(!account){
      console.log('ERROR: router.post. reset-password: '+util.inspect(account));
      return render.renderPartial(res, 
                                  next, 
                                  'reset_password', 
                                  { 
                                    password: 'hidden',
                                    login: 'hidden',
                                    error: '',
                                    alert: { 
                                              style: 'alert-error', 
                                              message: 'Could not find the user for given username'
                                            } 
                                  });
    }

    crypto.randomBytes(20, function(err, buffer) {
      if(err){
        return render.renderPartial(res, 
                      next, 
                      'reset_password', 
                      { 
                        password: 'hidden',
                        login: 'hidden',
                        error: '',
                        alert: { 
                                  style: 'alert-error', 
                                  message: err.message
                                } 
                      });
      }
      var token = buffer.toString('hex');
      account.resetPasswordToken = token;
      account.resetPasswordTokenExpires = Date.now() + 3600000; // 1 hour
      console.log('INFO: reset-password: '+JSON.stringify(account));
      account.save(function(err) {
        if(err){
          return render.renderPartial(res, 
                              next, 
                              'reset_password', 
                              { 
                                password: 'hidden',
                                login: 'hidden',
                                error: '',
                                alert: { 
                                          style: 'alert-error', 
                                          message: err.message
                                        } 
                              });
        }
        res.render('reset_password_email', {
            name: account.username,
            url: 'http://' + req.headers.host + '/reset-password?token=' + token
          }, function(err, emailHTML) {

          if(err){
            return render.renderPartial(res, 
                                next, 
                                'reset_password', 
                                {
                                  password: 'hidden',
                                  login: 'hidden',
                                  error: '',
                                  alert: { 
                                            style: 'alert-error', 
                                            message: err.message
                                          } 
                                });
          }
          mail.send(account.username, 
                  account.nickname, 
                  'admin@raptive.com', 
                  'admin@raptive.com', 
                  emailHTML, 
                  "Please Verify E-Mail address", 
                  function(err){
            if(err){
              return render.renderPartial(res, 
                                  next, 
                                  'reset_password', 
                                  { 
                                    password: 'hidden',
                                    login: 'hidden',
                                    error: '',
                                    alert: { 
                                              style: 'alert-error', 
                                              message: err.message
                                            } 
                                  });
            }
            return render.renderPartial(res, 
                                  next, 
                                  'reset_password', 
                                  {
                                    password: 'hidden',
                                    login: 'hidden',
                                    error: '',
                                    alert: { 
                                              style: 'alert-success', 
                                              message: 'Mail successfully sent'
                                            } 
                                  });
          });
        });
      });
    });
  });
});

router.get('/logout', function(template, req, res, next) {
    req.logout();
    res.redirect('/');
});

function authenticaAndRender(req, res, next){
  passport.authenticate('local',
                         function(err, account, info) {

    if (err) { 
      console.log('ERROR: router.post Error: '+ util.inspect(err));
      return render.renderPartial(res, next, template, { alert: { style: '', message: err.message } }); 
    }
    if (!account) { 
      console.log('ERROR: router.post. User: '+util.inspect(account));
      return render.renderPartial(res, 
                                  next, 
                                  template, 
                                  { 
                                    alert: { 
                                              style: '', 
                                              message: "User not found"
                                            } 
                                  }); 
    }
    console.log('INFO: router.post. User: '+util.inspect(account));
    //log the user in
    req.logIn(account, function(err) {
      if(err){
        console.log('ERROR: router.post.'+template+": "+ util.inspect(err));
        return render.renderPartial(res, next, template, { alert: { style: '', message: err.message } }); 
      }
      console.log('INFO: router.post.'+template+' User: '+util.inspect(account));
      render.renderProfilePage(err, account, res, next);
    });
  });
}
module.exports = router;
