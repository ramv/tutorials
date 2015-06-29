function renderPage(res, next, partial, data, layout, key){
  return res.render(partial, 
                    data, 
                    function(err,html){
     if(err){
       console.log(JSON.stringify(err));
       //return next(err);
     }
     var partialHtml = {};
     partialHtml[key] = html;
     res.render(layout, partialHtml, function(err,html){
        if(err){
          console.log(JSON.stringify(err));
          //return next(err);
        }
        res.send(html);   
     });     
  });
}

function renderPartial(res, next, partial, data){
  return renderPage(res, next, partial, data, 'layout', 'partialHTML');
}

function renderProfilePage(err, account, res, next){
  if (err) {
      return renderPartial(res, next, 'error', {
          message: err.message,
          error: err,
          detail: account
      });
  }
  if(account){
    var style = '';
    console.log('renderProfilePage account.emailVerified: '+account.emailVerified);
    if(account.emailVerified == 'true'){
      style = 'hidden';
    }
    console.log(JSON.stringify(account));
    return renderPartial(res, next, 'profile', {
      username: account.username, 
      nickname: account.nickname, 
      alert:{ 
        style: style, 
        message: 'Please verify your email address'}
    });
  }
}
module.exports = { 
                  renderPartial: renderPartial, 
                  renderPage: renderPage, 
                  renderProfilePage: renderProfilePage
                 };
