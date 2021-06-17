/**
 *
 * Project Name:	mqtt-client
 * File Name:	MqttV5Receiver.java
 *
 * Author:      Wang Huiyuan
 * Create Date: 2021年6月14日
 * Version:		1.0
 * Remark：
 */
package org.tiger.test.mqttClient;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangHuiyuan
 *
 */
public class MqttV5Receiver implements MqttCallback {
	
	private static final Logger logger = LoggerFactory.getLogger(MqttV5Receiver.class);

	private final PrintStream reportStream;
	private boolean connected = false;
	private String clientId;

	public class ReceivedMessage {

		/** */
		public String topic;
		/** */
		public MqttMessage message;

		ReceivedMessage(String topic, MqttMessage message) {
			this.topic = topic;
			this.message = message;
		}
	}
	
	List<ReceivedMessage> receivedMessages = new ArrayList<ReceivedMessage>();

	public MqttV5Receiver(String clientId, PrintStream reportStream) {

		this.reportStream = reportStream;
		connected = true;

		this.clientId = clientId;

	}
	
	public synchronized ReceivedMessage receiveNext(long waitMilliseconds) throws InterruptedException {
				
		ReceivedMessage receivedMessage = null;
		if (receivedMessages.isEmpty()) {
		  wait(waitMilliseconds);
		}
		if (!receivedMessages.isEmpty()) {
		  receivedMessage = receivedMessages.remove(0);
		}
		
		return receivedMessage;
	}
	

	public boolean validateReceipt(String sendTopic, int expectedQos, MqttMessage message) throws MqttException, InterruptedException {
		return validateReceipt(sendTopic, expectedQos, message.getPayload());
	}
	  
	  
	public boolean validateReceipt(String sendTopic, int expectedQos, byte[] sentBytes) throws MqttException, InterruptedException {


	    long waitMilliseconds = 40*30000;
	    ReceivedMessage receivedMessage = receiveNext(waitMilliseconds);
	    if (receivedMessage == null) {
	      logger.info(" No message received in waitMilliseconds={}", waitMilliseconds);
	      return false;
	    }

	    if (!sendTopic.equals(receivedMessage.topic)) {
	      logger.info(" Received invalid topic sent={} received topic={}", sendTopic, receivedMessage.topic);
	      return false;
	    }

	    if (!java.util.Arrays.equals(sentBytes,
	        receivedMessage.message.getPayload())) {
	      logger.info("Received invalid payload={}", Arrays.toString(receivedMessage.message.getPayload()));
	      logger.info("Sent: {}", new String(sentBytes));
	      logger.info("Received:{}", new String(receivedMessage.message.getPayload()));
	      return false;
	    }

	    if (expectedQos != receivedMessage.message.getQos()) {
	      logger.info("expectedQos={} != Received Qos={}", expectedQos, receivedMessage.message.getQos());
	      return false;
	    }

	    return true;
	}

	@Override
	public void disconnected(MqttDisconnectResponse disconnectResponse) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.mqttv5.client.MqttCallback#mqttErrorOccurred(org.eclipse.
	 * paho.mqttv5.common.MqttException)
	 */
	@Override
	public void mqttErrorOccurred(MqttException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {

		// logger.fine(methodName + ": '" + new String(message.getPayload()) + "'");
		receivedMessages.add(new ReceivedMessage(topic, message));
		notify();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.paho.mqttv5.client.MqttCallback#deliveryComplete(org.eclipse.paho
	 * .mqttv5.client.IMqttToken)
	 */
	@Override
	public void deliveryComplete(IMqttToken token) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.mqttv5.client.MqttCallback#connectComplete(boolean,
	 * java.lang.String)
	 */
	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.paho.mqttv5.client.MqttCallback#authPacketArrived(int,
	 * org.eclipse.paho.mqttv5.common.packet.MqttProperties)
	 */
	@Override
	public void authPacketArrived(int reasonCode, MqttProperties properties) {
		// TODO Auto-generated method stub

	}
}
