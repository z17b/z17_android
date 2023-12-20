package cu.z17.singledi

class SinglediException: Exception("Instance requested before created. Be sure to use createInstance method on your main App class.")