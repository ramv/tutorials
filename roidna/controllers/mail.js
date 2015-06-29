var mandrill = require('mandrill-api/mandrill');
var mandrill_client = new mandrill.Mandrill('4MrirRu1tGO5gvKlZonpPA');

//TODO move this to a config file
var mandrill = {
  host: 'smtp.mandrillapp.com',
  port: 587,
  username: 'rviswanadha@gmail.com',
  password: '4MrirRu1tGO5gvKlZonpPA'
};

function send(to, toName, from, replyTo, html, subject, cb){
  var message = {
    "html": html,
    "subject": subject,
    "from_email": from,
    "from_name": "No Reply",
    "to": [{
            "email": to,
            "name": toName,
            "type": "to"
        }],
    "headers": {
        "Reply-To": replyTo
    },
    "important": false,
    "track_opens": null,
    "track_clicks": null,
    "auto_text": null,
    "auto_html": null,
    "inline_css": null,
    "url_strip_qs": null,
    "preserve_recipients": null,
    "view_content_link": null,
    //"bcc_address": "message.bcc_address@example.com",
    "tracking_domain": null,
    "signing_domain": null,
    "return_path_domain": null,
    "merge": true,
    "merge_language": "mailchimp",
    "global_merge_vars": [{
            "name": "merge1",
            "content": "merge1 content"
        }],
    "merge_vars": [{
            "rcpt": "recipient.email@example.com",
            "vars": [{
                    "name": "merge2",
                    "content": "merge2 content"
                }]
        }],
    "tags": [
        "password-resets"
    ],
    "subaccount": "customer-123",
    "google_analytics_domains": [
        "example.com"
    ],
    "google_analytics_campaign": "message.from_email@example.com",
    "metadata": {
        "website": "www.example.com"
    },
    "recipient_metadata": [{
            "rcpt": "recipient.email@example.com",
            "values": {
                "user_id": 123456
            }
        }],
    "attachments": [{
            "type": "text/plain",
            "name": "myfile.txt",
            "content": "ZXhhbXBsZSBmaWxl"
        }],
    "images": [{
            "type": "image/png",
            "name": "IMAGECID",
            "content": "ZXhhbXBsZSBmaWxl"
        }]
  };
  var async = false;
  var ip_pool = "Main Pool";
  var send_at = Date.now();
  mandrill_client.messages.send({"message": message, "async": async, "ip_pool": ip_pool}, function(result) {
      console.log('INFO: Successfully sent email: ' + JSON.stringify(result));
      cb(null);
  }, function(e) {
      // Mandrill returns the error as an object with name and message keys
      console.log('A mandrill error occurred: ' + e.name + ' - ' + e.message);
      cb(e);
  });
}
  
module.exports = {send:  send};