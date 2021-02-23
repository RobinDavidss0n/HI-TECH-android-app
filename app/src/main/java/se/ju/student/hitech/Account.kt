package se.ju.student.hitech

class Account (var name: String, var role: String) {

    public fun updateName (newName: String){
        name = newName
        updateFirebase()
    }

    public fun updateRole (newRole: String){
        role = newRole
        updateFirebase()
    }

    private fun updateFirebase() {
        TODO("Not yet implemented")
    }

}