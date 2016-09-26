package ir.sharif.ce.partov.user;

import java.util.Scanner;

import ir.sharif.ce.partov.base.ClientFramework;
import ir.sharif.ce.partov.base.Frame;
import ir.sharif.ce.partov.base.Machine;
import ir.sharif.ce.partov.utils.Utility;

public class SimulateMachine extends Machine {
	public SimulateMachine(ClientFramework clientFramework, int count) {
		super(clientFramework, count);
		// The machine instantiated.
		// Interfaces are not valid at this point.
	}

	public void initialize() {
		// TODO: Initialize your program here; interfaces are valid now.
	}

	/**
	 * This method is called from the main thread. Also ownership of the data of
	 * the frame is not with you. If you need it, make a copy for yourself.
	 */
	public void processFrame(Frame frame, int ifaceIndex) {
		// TODO: process the raw frame; frame.data points to the frame byte

		//make a copy of frame data
		byte[] data = new byte[frame.data.length];
		System.arraycopy(frame.data, 0, data, 0, data.length);
		
		//simple frame data processing sample
		System.out.println("Frame received at iface " + ifaceIndex
		+ " with length " + frame.length );
		EthernetHeader eth = new EthernetHeader(data, 0);
		System.out.println("type of ethernet packet is 0x"+ Utility.byteToHex(eth.getTypeinBytes()[0])+Utility.byteToHex(eth.getTypeinBytes()[1]));
		if(eth.getTypeinInt()==((int)IPv4Header.IP_PROTOCOL)){
			IPv4Header iph = new IPv4Header(data, 14, 5);	//Packet, Position of IP header in packet, IP header Length in Word unit(4 bytes)
			System.out.println("Source IP: "+ Utility.getIPString(iph.getSrc()));
			System.out.println("Destination IP: "+ Utility.getIPString(iph.getDest()));
		}
	}

	/**
	 * This method will be run from an independent thread. Use it if needed or
	 * simply return. Returning from this method will not finish the execution
	 * of the program.
	 */
	public void run() {
		// TODO: write you business logic here...
		
		Scanner s = new Scanner(System.in);
		System.out.println("Type \"send\" to send a sample frame.");
		while(!s.nextLine().equals("send"));
		
		EthernetHeader eth = new EthernetHeader(EthernetHeader.BROADCAST, iface[0].mac, IPv4Header.IP_PROTOCOL);
		IPv4Header iph = new IPv4Header();
		iph.setTotalLength(100);
		iph.setSrc(iface[0].ip);
		iph.setDest(IPv4Header.BROADCAST_IP);
		iph.setTTL(64);
//		iph.setProtocol(protocol);
//		iph.setChecksum(HeaderChecksum);
//		...
		
		byte[] data = new byte[14+100];
		System.arraycopy(eth.getData(), 0, data, 0, 14);
		System.arraycopy(iph.getData(), 0, data, 14, iph.getData().length);
		this.sendFrame(new Frame(114,data), 0);
		System.out.println("Sample frame sent.");
		s.close();
	}

}