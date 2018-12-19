const functions = require('firebase-functions');
const moment = require('moment-timezone');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------

exports.reportWatcher = functions.database.ref('/REPORTS/{reportId}')
    .onWrite((change, context) => {		
		const reportBefore = change.before.val();
		const reportAfter = change.after.val();
		var subject = "";		
		var text = "";
		var userId = "";		
		
		if(!change.before.exists() && change.after.exists()) { // create			
			subject = "Report changed by "+ reportAfter.userName;			
			text = "  REPORT CREATED\n"+ getReportInfo(reportAfter);
			userId = reportAfter.userId;			
		}
		else if (change.before.exists() && change.after.exists()) { // update
			subject = "Report changed by "+ reportAfter.userName;
			text = "  REPORT UPDATED\n"+ getReportInfo(reportAfter) + "\n  OLD REPORT\n" + getReportInfo(reportBefore);
			userId = reportAfter.userId;			
		}
		else if (!change.after.exists()) { // delete
			subject = "Report changed by "+ reportBefore.userName;	
			text = "  REPORT REMOVED\n"+ getReportInfo(reportBefore);
			userId = reportBefore.userId;			
			}
		if(!userId) return null;
		console.log(subject, text);
		const usersPromise = admin.database().ref("/USERS").child(userId).child("isAllowEditPastReports").once('value');
		return usersPromise.then((snapshot)=>{
			if(snapshot.val()){
				console.log("EMAIL SENT!", userId);
				return sendModeratorEmail(subject, text);	
			} else {				
				return null;
			}
		});
	});
	
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------

exports.userPushWatcher = functions.database.ref('/USERS/{userId}/pushHistory/{pushId}')
.onCreate((change, context) => {		
	const pushDetails = change.val();
	const usersPromise = admin.database().ref("/USERS").child(context.params.userId).once('value');
	return usersPromise.then((snapshot)=>{
		var tokens = [];		
			if(snapshot.val().token) {
				tokens.push(snapshot.val().token);
				var payload = {
					notification:{
						title: pushDetails.title,
						body: pushDetails.body,
						type: "direct_reminder",
						"content_available" : "1",
						badge: "1"
					}
				}	
				console.log("Sending direct push");	
				return sendMessagesViaFCM(tokens, payload);
			} else {
				return console.log("Direct push not sent. Maybe there is no token!");		
			}
		
	});

});
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
exports.turnOffEditMode = functions.https.onRequest((request, response) => {   
	const usersPromise = admin.database().ref("/USERS").once('value');
	return usersPromise.then(usersSnap => {
			var count = 0;
			usersSnap.forEach(user => {	
				var userObj = user.val();	
				if(userObj.isAllowEditPastReports){
					admin.database().ref(`/USERS/${userObj.key}/isAllowEditPastReports`).set(false);
					console.log("Changed value for: " + userObj.key);
					count = count + 1;
				}
			});
			return response.send("Users with enabled edit mode : " + count );
	});
 });
 // ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
exports.pushTest = functions.https.onRequest((request, response) => {   
	const settingsPromise = admin.database().ref("/PUSH_TEST").once('value');
	return settingsPromise.then(settingsSnap => {
		var settingsObj = settingsSnap.val();
		const userId = settingsObj.userId;
		const title = settingsObj.title;
		const body = settingsObj.body;
		const userPromise = admin.database().ref("/USERS").child(userId).once('value');
		return userPromise.then(userSnap => {
			var userObj = userSnap.val();
			var tokens = [];
			if(userObj.token) {
				tokens.push(userObj.token);
				var payload = {
					notification:{
						title: title,
						body: body,
						type: "yesterday_reminder",
						"content_available" : "1",
						badge: "1"
					}
				}
				sendMessagesViaFCM(tokens, payload);
				console.log("Push Sent");
				return response.send("Push Sent to the user with id = " + userId + ". And token = " + userObj.token);		
			} else {
				console.log("Push Not Sent");
				return response.send("Push Not Send to the user with id = " + userId + ". Maybe there is no token!");		
			}
		});	
	});
});
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ---------------------------------------------------------------------------- 
exports.reminder = functions.https.onRequest((request, response) => {   
	const dateStr = moment().format("YYYYMMDD"); 
	const dateFrom = moment(dateStr, "YYYYMMDD");
	const hourStr = moment().tz('Europe/Kiev').format("HH");
	const minuteStr = moment().tz('Europe/Kiev').format("mm");
	const hourAndTimeStr = moment().tz('Europe/Kiev').format("HH:mm");
	const reportsPromise = admin.database().ref("/REPORTS").orderByKey().limitToLast(300).once('value');
	const usersPromise = admin.database().ref("/USERS").once('value');
	const holidaysPromise = admin.database().ref("/HOLIDAYS").once('value');
	return Promise.all([reportsPromise, usersPromise, holidaysPromise]).then((result)=> {		
		const reportsSnap =result[0];
		const usersSnap =result[1];
		const holidaysSnap =result[2];
		if(holidaysSnap.child(dateStr).val()){
			console.log("Today is Holiday!" );			
			return response.send("Today is Holiday!");
		}
		var tokens = [];
		var count = 0;
		var sentCount = 0;
		var missedCount = 0;
		var logText = "";
		usersSnap.forEach(user => {	
			var userObj = user.val();
			var aKey = dateStr + "_" + userObj.key;			
			if(reportsSnap.child(aKey).val()){ 
				count  = count + 1;
				logText = logText + "FOUND " + userObj.name + " (" + aKey+ ")\n";
			}else{
				missedCount = missedCount + 1;
				logText = logText + "MISSEDD "+ userObj.name + " (" + aKey+ ")\n";
				if(userObj.remindMeAt){
					if(userObj.remindMeAt === hourAndTimeStr){
						logText = logText + "USER REMIND ME AT SETTINGS " + userObj.remindMeAt+"\n";
						if(!userObj.isAdmin && userObj.token){
							sentCount  = sentCount + 1;
							tokens.push(userObj.token);
						}else{
							logText = logText +"USER TOKEN IS EMPTY\n";
						}
					}
				}else if(userObj.notificationHour === hourStr && minuteStr === "00"){
					logText = logText +"USER HOUR SETTINGS " + userObj.notificationHour+"\n";
					if(!userObj.isAdmin && userObj.token){
						sentCount  = sentCount + 1;
						tokens.push(userObj.token);
					}else{
						logText = logText +"USER TOKEN IS EMPTY\n";
					}
				}
			}
		});
		var payload = {
			notification:{
				title: "It's time to fill in the Workload!",
				body: "It won't take more than one minute",
				type: "reminder",
				"content_available" : "1",
				badge: "1"
			}			
		}			
		console.log("Europe/Kiev = " + hourAndTimeStr + "; Filled Workloads: " + count + "; Missed " + missedCount + "; Push Sent " + sentCount + "\n" + logText);
		response.send("Europe/Kiev = " + hourAndTimeStr + "; Filled Workloads : " + count + "; Missed " + missedCount + "; Push Sent " + sentCount + "\n" + logText);	
		return sendMessagesViaFCM(tokens, payload);		
	});  
 });
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
 exports.yesterdayReminder = functions.https.onRequest((request, response) => {   
	const days = request.query.days;	
	const type = request.query.type;
	var message = "fill in the workload for yesterday!"
	if(type === "last"){
		message = "last chance to fill in the workload for yesterday!"
	}
	const dateStr = moment().subtract(days, 'day').format("YYYYMMDD"); 
	const reportsPromise = admin.database().ref("/REPORTS").orderByKey().limitToLast(300).once('value');
	const usersPromise = admin.database().ref("/USERS").once('value');
	const holidaysPromise = admin.database().ref("/HOLIDAYS").once('value');
  
	return Promise.all([reportsPromise, usersPromise, holidaysPromise]).then((result)=> {		
		const reportsSnap =result[0];
		const usersSnap =result[1];
		const holidaysSnap =result[2];
		if(holidaysSnap.child(dateStr).val()){
			console.log("Today is Holiday!" );			
			return response.send("Today is Holiday!");
		}
		var logText = "";
		var count = 0;
		var sentCount = 0;
		var missedCount = 0;
		usersSnap.forEach(user => {	
			var userObj = user.val();
			var aKey = dateStr + "_" + userObj.key;			
			if(reportsSnap.child(aKey).val()){ 
				count  = count + 1;
				logText = logText + "FOUND " + userObj.name + " (" + aKey+ ")\n";
			}else{
				missedCount = missedCount + 1;
				logText = logText + "MISSEDD " + userObj.name + " (" + aKey+ ")\n";		
				if(!userObj.isAdmin && userObj.token){
					sentCount  = sentCount + 1;
					var tokens = [];
					tokens.push(userObj.token);
					var payload = {
						notification:{
							title: userObj.name + ", " + message,
							body: "It won't take more than one minute",
							type: "yesterday_reminder",
							"content_available" : "1",
							badge: "1"
						}
					}
					if(type === "first" || type === "last" || userObj.name==="Ruslan Lyalko") 
						sendMessagesViaFCM(tokens, payload);					
				}else{
					logText = logText + "USER TOKEN IS EMPTY\n";
				}
			}
		});
		console.log("Check Date " + dateStr + "; Filled Workloads " + count + "; Missed " + missedCount + "; Push Sent " + sentCount + "\n" + logText);
		return response.send("Check Date " + dateStr + "; Filled Workloads " + count + "; Missed " + missedCount + "; Push Sent " + sentCount + "\n" + logText);			
	});  
 });
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
exports.getProjectInfo = functions.https.onCall((data, context) => {
	// Checking that the user is authenticated.
	if (!context.auth) {	  
	  throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
		  'while authenticated.');
	}
	const project = data.project;
	const from = data.from;
	const to = data.to;
	// Checking attribute.
	if (!(typeof project === 'string') || project.length === 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, first "project" should containing the project title.');
	}
	if (!(typeof from === 'number') || from < 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, second "from" should containing the from date.');
	}
	if (!(typeof to === 'number') || to < 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, third "to" should containing the to date.');
	}
	// results data
	var iOSCount = 0;
	var AndroidCount = 0;	
	var BackendCount = 0;
	var DesignCount = 0;
	var PMCount = 0;
	var QACount = 0;
	var OtherCount = 0;
	const reportsPromise = admin.database().ref("/REPORTS").once('value');
	return reportsPromise.then(reportsSnap => {
			reportsSnap.forEach(report => {	
				var reportObj = report.val();			
				if(reportObj.date.time >from && reportObj.date.time < to){
					if(reportObj.t1 > 0 && reportObj.p1 === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.t1;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.t1;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.t1;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.t1;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.t1;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.t1;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.t1;
					}
					if(reportObj.t2 > 0 && reportObj.p2 === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.t2;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.t2;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.t2;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.t2;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.t2;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.t2;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.t2;
					}
					if(reportObj.t3 > 0 && reportObj.p3 === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.t3;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.t3;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.t3;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.t3;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.t3;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.t3;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.t3;
					}
					if(reportObj.t4 > 0 && reportObj.p4 === project){
						if(reportObj.userDepartment === 'iOS')
							iOSCount = iOSCount + reportObj.t4;
						if(reportObj.userDepartment === 'Android')
							AndroidCount = AndroidCount + reportObj.t4;
						if(reportObj.userDepartment === 'Backend,Web')
							BackendCount = BackendCount + reportObj.t4;
						if(reportObj.userDepartment === 'Design')
							DesignCount = DesignCount + reportObj.t4;
						if(reportObj.userDepartment === 'PM')
							PMCount = PMCount + reportObj.t4;
						if(reportObj.userDepartment === 'QA')
							QACount = QACount + reportObj.t4;
						if(reportObj.userDepartment === 'Other')
							OtherCount = OtherCount + reportObj.t4;
					}
				}
			});	
			return {
				iOS: iOSCount,
				Android: AndroidCount,
				Backend: BackendCount,
				Design: DesignCount,
				PM: PMCount,
				QA: QACount,
				Other: OtherCount	
			}	
		});	
});
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
exports.isBlocked = functions.https.onCall((data, context) => {
	// Checking that the user is authenticated.
	if (!context.auth) {	  
	  throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
		  'while authenticated.');
	}
	const userIsBlockedPromise = admin.database().ref("/USERS").child(context.auth.uid).child("isBlocked").once('value');
	return userIsBlockedPromise.then(snap => {
		const isBlocked = snap.val(); 
			return {
				isBlocked: isBlocked
			}	
		});	
});
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
// ----------------------------------------------------------------------------
exports.isRightDate = functions.https.onCall((data, context) => {
	// Checking that the user is authenticated.
	if (!context.auth) {	  
	  throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
		  'while authenticated.');
	}
	const date = data.date;
	// Checking attribute.
	if (!(typeof date === 'number') || date < 0) {	  
	  throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
		  'three arguments, first "project" should containing the project title.');
	}
	const isRight = moment(date).format("DDMMYYYY") === moment().format("DDMMYYYY");
	return {
		isRight: isRight
	}			
});
// ----------------------------------------------------------------------------
function getReportInfo(report){
	return "Status: "+ report.status + "\n"+
			"Date: "+ moment(report.date.time).format("DD.MM.YYYY") + "\n"+
			"Project 1: "+ report.p1 + "; time: "+report.t1 + "\n"+
			"Project 2: "+ report.p2 + "; time: "+report.t2 + "\n"+
			"Project 3: "+ report.p3 + "; time: "+report.t3 + "\n"+
			"Project 4: "+ report.p4 + "; time: "+report.t4 + "\n"+
			"Department: "+ report.userDepartment + "\n"+
			"User Id: "+ report.userId + "\n";
}
// ----------------------------------------------------------------------------
function sendMessagesViaFCM(tokens, payload){
	if(tokens.length > 0)
		return  admin.messaging().sendToDevice(tokens, payload)
			.then(response => {
				console.log("Push Sent: ", response);
				return 0;
			})
			.catch(error => {
				console.log("Push Error: ", error);
			});			
	else 
		return console.log("Push No Tokens");
}
// ----------------------------------------------------------------------------
function sendModeratorEmail(subject, text){
	const settingsPromise = admin.database().ref("/SETTINGS").once('value');
	return settingsPromise.then((snapshot)=>{
		if(snapshot.val().notificationEmail){
			console.log("Sending email for ", snapshot.val().notificationEmail);
			return sendEmail(snapshot.val().notificationEmail, subject, text);
		} else {
			return console.log("Notification Email is empty!");
		}
	});
}
// ----------------------------------------------------------------------------
function sendEmail(to, subject, text){
	//firebase functions:config:set gmail.email= "someemail@gmail.com"
	//firebase functions:config:set gmail.password= "somepassword"
	const gmailEmail = functions.config().gmail.email;
	const gmailPassword = functions.config().gmail.password;
	const mailTransport = nodemailer.createTransport({
		service: 'gmail',
		auth: {
			user: gmailEmail,
			pass: gmailPassword,
		},
	});
	const mailOptions = {
		from: '"PettersonApps Workload" <pa.workload@gmail.com>',
		to: to,
		subject: subject,
		text: text
	};
	return mailTransport.sendMail(mailOptions).then(response => {
		console.log("Email Sent: ", response);
		return 0;
	})
	.catch(error => {
		console.log("Email Error: ", error);
	});		   
}

/*
exports.userWatcher = functions.database.ref('/USERS/{userId}')
    .onWrite((change, context) => {		
		if(change.after.exists()) { 
			const dateFormatted= moment(change.after.child("birthday/time").val()).format("HH:mm:ss DD.MM.YYYY");
			console.log(context.params.userId, dateFormatted);			
			change.after.ref.child("birthday/formatted").set(dateFormatted);
			const firstFormatted= moment(change.after.child("firstWorkingDate/time").val()).format("HH:mm:ss DD.MM.YYYY");
			console.log(context.params.userId, firstFormatted);			
			return change.after.ref.child("firstWorkingDate/formatted").set(firstFormatted);
		}
		else
		 return null;
});
*/
