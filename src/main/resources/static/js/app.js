
(function() {
	
	var app = angular.module('qbot', []);
	
	app.controller("QbotController", function($scope) {

		this.findAnswer = function(value){
			var ans = questions[value];			
			if (typeof ans == 'string' && ans.indexOf("$") == 0){
				return eval(ans.substr(1) + "()");								
			} 
			return ans;
		}
	
	});
	
	var questions = {
		"what is your name" : "my name is qbot",
		"what time is it" : "$findTime"
	};

	this.findTime = function(){
		return "right now it's " + new Date();
	}	

})();


