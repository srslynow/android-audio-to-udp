package hw.dt83.udpchat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class AudioCall {

	private static final String LOG_TAG = "AudioCall";
	private static final int SAMPLE_RATE = 44100; // Hertz
	private static final int SAMPLE_INTERVAL = 20; // Milliseconds
	private InetAddress address; // Address to call
	private int port = 50000; // Port the packets are addressed to
	private boolean mic = false; // Enable mic?
	private boolean speakers = false; // Enable speakers?
	
	public AudioCall(InetAddress address) {
		
		this.address = address;
	}
	
	public void startCall() {
		
		startMic();
	}
	
	public void endCall() {
		
		Log.i(LOG_TAG, "Ending call!");
		muteMic();
		muteSpeakers();
	}
	
	public void muteMic() {
		
		mic = false;
	}
	
	public void muteSpeakers() {
		
		speakers = false;
	}
	
	public void startMic() {
		// Creates the thread for capturing and transmitting audio
		mic = true;
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// Create an instance of the AudioRecord class
				int BUF_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
				Log.i(LOG_TAG, "Send thread started. Thread id: " + Thread.currentThread().getId());
				AudioRecord audioRecorder = new AudioRecord (MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
						AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE);
				int bytes_read = 0;
				int bytes_sent = 0;
				byte[] buf = new byte[BUF_SIZE];
				try {
					// Create a socket and start recording
					Log.i(LOG_TAG, "Packet destination: " + address.toString());
					DatagramSocket socket = new DatagramSocket();
					audioRecorder.startRecording();
					while(mic) {
						// Capture audio from the mic and transmit it
						bytes_read = audioRecorder.read(buf, 0, BUF_SIZE);
						DatagramPacket packet = new DatagramPacket(buf, bytes_read, address, port);
						socket.send(packet);
						bytes_sent += bytes_read;
						Log.i(LOG_TAG, "Total bytes sent: " + bytes_sent);
					}
					// Stop recording and release resources
					audioRecorder.stop();
					audioRecorder.release();
					socket.disconnect();
					socket.close();
					mic = false;
					return;
				}
				catch(SocketException e) {
					
					Log.e(LOG_TAG, "SocketException: " + e.toString());
					mic = false;
				}
				catch(UnknownHostException e) {
					
					Log.e(LOG_TAG, "UnknownHostException: " + e.toString());
					mic = false;
				}
				catch(IOException e) {
					
					Log.e(LOG_TAG, "IOException: " + e.toString());
					mic = false;
				}
			}
		});
		thread.start();
	}
}
