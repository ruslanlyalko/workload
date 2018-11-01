const functions = require('firebase-functions');
const moment = require('moment-timezone');
const admin = require('firebase-admin');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//

 
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


 exports.yesterdayReminder = functions.https.onRequest((request, response) => {   
 //	const key = req.query.key;
	//firebase functions:config:set cron.key="somecoolkey"
	// Exit if the keys don't match.
//	if (key !== functions.config().cron.key) {
//		console.log('The key provided in the request does not match the key set in the environment. Check that', key,
//			'matches the cron.key attribute in `firebase env:get`');
//		res.status(403).send('Security key does not match. Make sure your "key" URL query parameter matches the ' +
//			'cron.key environment variable.');
//		return null;
//	}
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
		var count = 0;
		var sentCount = 0;
		var missedCount = 0;
			
		usersSnap.forEach(user => {	
			var userObj = user.val();
			var aKey = dateStr + "_" + userObj.key;			
			if(reportsSnap.child(aKey).val()){ 
				count  = count + 1;
				console.log("FOUND ", userObj.name + " (" + aKey+ ")");
			}else{
				missedCount = missedCount + 1;
				console.log("MISSEDD ", userObj.name + " (" + aKey+ ")");		
				if(!userObj.isAdmin && userObj.token){
					sentCount  = sentCount + 1;
					var tokens = [];
					tokens.push(userObj.token);
					var payload = {
						data:{
							title: userObj.name + ", " + message,
							body: "It won't take more than one minute",
							type: "yesterday_reminder"
						}
					}
					if(type === "first" || type === "last" || userObj.name==="Ruslan Lyalko") 
						sendMessagesViaFCM(tokens, payload);					
				}else{
					console.log("USER TOKEN IS EMPTY");
				}
				
			}
		});
	
		console.log("Check Date " + dateStr + "; Filled Workloads " + count + "; Missed " + missedCount + "; Push Sent " + sentCount);
		return response.send("Check Date " + dateStr + "; Filled Workloads " + count + "; Missed " + missedCount + "; Push Sent " + sentCount);			
	});  
 });

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
			
		usersSnap.forEach(user => {	
			var userObj = user.val();
			var aKey = dateStr + "_" + userObj.key;			
			if(reportsSnap.child(aKey).val()){ 
				count  = count + 1;
				console.log("FOUND ", userObj.name + " (" + aKey+ ")");
			}else{
				missedCount = missedCount + 1;
				console.log("MISSEDD ", userObj.name + " (" + aKey+ ")");
				if(userObj.remindMeAt){
					if(userObj.remindMeAt === hourAndTimeStr){
						console.log("USER REMIND ME AT SETTINGS ", userObj.remindMeAt);
						if(!userObj.isAdmin && userObj.token){
							sentCount  = sentCount + 1;
							tokens.push(userObj.token);
						}else{
							console.log("USER TOKEN IS EMPTY");
						}
					}
					
				}else if(userObj.notificationHour === hourStr && minuteStr === "00"){
					console.log("USER HOUR SETTINGS ", userObj.notificationHour);
					if(!userObj.isAdmin && userObj.token){
						sentCount  = sentCount + 1;
						tokens.push(userObj.token);
					}else{
						console.log("USER TOKEN IS EMPTY");
					}
				}
			}
		});
	
		var payload = {
			data:{
				title: "It's time to fill in the Workload!",
				body: "It won't take more than one minute",
				type: "reminder"
			}			
		}	

		console.log("Europe/Kiev = " + hourAndTimeStr + "; Filled Workloads: " + count + "; Missed " + missedCount + "; Push Sent " + sentCount);
		response.send("Europe/Kiev = " + hourAndTimeStr + "; Filled Workloads : " + count + "; Missed " + missedCount + "; Push Sent " + sentCount);	
		
		return sendMessagesViaFCM(tokens, payload);		
	});  
 });



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