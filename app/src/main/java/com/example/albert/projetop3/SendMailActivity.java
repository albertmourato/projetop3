package com.example.albert.projetop3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class SendMailActivity extends Activity {
	Thread thread;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		thread = new Thread(){
			@Override
			public void run() {
				while(true){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Context mContext = getApplicationContext();
							String fromEmail = "ams11@cin.ufpe.br";
							String fromPassword = "********";
							String toEmail = "ams11@cin.ufpe.br";
							String emailSubject = "Quero Férias";
							String emailBody = "Leopoldo, me passe!";
							List<String> toEmailList = Arrays.asList("ams11@cin.ufpe.br");
							new SendMailTask(SendMailActivity.this).execute(fromEmail,
									fromPassword, toEmailList, emailSubject, emailBody);
						}
					});
					try{
						sleep(35000);
					}catch (Exception e){
						Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
					}
				}
			}
		};





		//final Button send = (Button) this.findViewById(R.id.button1);
	/*
		send.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.i("SendMailActivity", "Send Button Clicked.");

				String fromEmail = ((TextView) findViewById(R.id.editText1))
						.getText().toString();
				String fromPassword = ((TextView) findViewById(R.id.editText2))
						.getText().toString();
				String toEmails = ((TextView) findViewById(R.id.editText3))
						.getText().toString();
				List<String> toEmailList = Arrays.asList(toEmails
						.split("\\s*,\\s*"));
				Log.i("SendMailActivity", "To List: " + toEmailList);
				String emailSubject = ((TextView) findViewById(R.id.editText4))
						.getText().toString();
				String emailBody = ((TextView) findViewById(R.id.editText5))
						.getText().toString();
				new SendMailTask(SendMailActivity.this).execute(fromEmail,
						fromPassword, toEmailList, emailSubject, emailBody);
			}
		});*/
	}
}
