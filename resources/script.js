var i = i || 0;
var fun1 = function(name) {
	print('Hi there from Javascript, ' + name);
	return "greetings from javascript";
};

var fun2 = function(object) {
	i++;
	print("count: " + i + "/" + object);
	return "hello";
};