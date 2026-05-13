import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Usuari
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository {
    private val apiService = RetrofitClient.instance

    fun loginUser(user: String, pass: String, onResult: (Usuari?, String?) -> Unit) {
        val loginData = LoginRequest(user, pass)

        apiService.login(loginData).enqueue(object : Callback<Usuari> {
            override fun onResponse(call: Call<Usuari>, response: Response<Usuari>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Error: Credenciales inválidas")
                }
            }

            override fun onFailure(call: Call<Usuari>, t: Throwable) {
                onResult(null, "Error de red: ${t.message}")
            }
        })
    }
}