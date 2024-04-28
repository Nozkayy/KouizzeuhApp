package fr.nozkay.firstapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.Normalizer
import java.util.Locale

class LobbyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val code = intent.getStringExtra("code")
        val cod = findViewById<TextView>(R.id.code)
        cod.text = code
        val gameRef = Firebase.firestore.collection("Game").document(code.toString())
        val math = findViewById<Button>(R.id.math)
        val geo = findViewById<Button>(R.id.geo)
        val pseudo = intent.getStringExtra("pseudo")
        val anglais = findViewById<Button>(R.id.anglais)
        var mdj = "Math"
        math.setOnClickListener{
            gameRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val host = document.getString("host").toString()
                        if(host.equalsIgnoreCaseWithAccent(pseudo.toString())){
                            math.setBackgroundColor(resources.getColor(R.color.bleu))
                            geo.setBackgroundColor(resources.getColor(R.color.background))
                            anglais.setBackgroundColor(resources.getColor(R.color.background))
                            mdj = "Math"
                            gameRef.update("mdj","Math")
                        }
                        else{
                            Toast.makeText(this, "Vous n'êtes pas l'host de la partie !", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
        anglais.setOnClickListener{
            gameRef.get()
            .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val host = document.getString("host").toString()
                        if(host.equalsIgnoreCaseWithAccent(pseudo.toString())){
                            math.setBackgroundColor(resources.getColor(R.color.background))
                            geo.setBackgroundColor(resources.getColor(R.color.background))
                            anglais.setBackgroundColor(resources.getColor(R.color.bleu))
                            mdj = "Anglais"
                            gameRef.update("mdj","Anglais")
                        }
                        else{
                            Toast.makeText(this, "Vous n'êtes pas l'host de la partie !", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
        geo.setOnClickListener{
            gameRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val host = document.getString("host").toString()
                        if(host.equalsIgnoreCaseWithAccent(pseudo.toString())){
                            math.setBackgroundColor(resources.getColor(R.color.background))
                            geo.setBackgroundColor(resources.getColor(R.color.bleu))
                            anglais.setBackgroundColor(resources.getColor(R.color.background))
                            mdj = "Geo"
                            gameRef.update("mdj","Geo")
                        }
                        else{
                            Toast.makeText(this, "Vous n'êtes pas l'host de la partie !", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
        val start = findViewById<Button>(R.id.startgame)
        start.setOnClickListener{ gameRef.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val host = document.getString("host").toString()
                        if(host.equalsIgnoreCaseWithAccent(pseudo.toString())){
                            gameRef.update("start", true)
                        }
                        else{
                            Toast.makeText(this, "Vous n'êtes pas l'host de la partie !", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
        val handler = Handler()
        val pl = findViewById<TextView>(R.id.nbjoueur)
        val runnable = object : Runnable {
            override fun run() {
                gameRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val start = documentSnapshot.getBoolean("start")
                            val players = documentSnapshot.get("players") as? List<String> ?: emptyList()
                            var texte = "Joueurs: ${players.size}/20\n"
                            for(i in players){
                                texte += "${i}, "
                            }
                            pl.text = texte
                            if (start == true) {
                                gameRef.get().addOnSuccessListener { document -> if (document != null && document.exists()) {
                                    val intentToGame = Intent(this@LobbyActivity, GameActivity::class.java)
                                    intentToGame.putExtra("mdj", document.getString("mdj").toString())
                                    intentToGame.putExtra("code", code)
                                    intentToGame.putExtra("pseudo", pseudo)
                                    startActivity(intentToGame)
                                }}
                                handler.removeCallbacks(this)
                            }
                        } else {
                            Log.d(TAG, "Le document n'existe pas")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Erreur lors de la récupération du document", e)
                    }
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(runnable, 1000)

    }

    fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}".toRegex(), "")
            .toLowerCase(Locale.getDefault())
    }

    fun String.equalsIgnoreCaseWithAccent(other: String): Boolean {
        return this.normalize() == other.normalize()
    }
}