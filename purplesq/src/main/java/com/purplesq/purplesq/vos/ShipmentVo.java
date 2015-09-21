package com.purplesq.purplesq.vos;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nishant on 20/09/15.
 */
public class ShipmentVo {

    private ReceiverVo receiver;
    private AddressVo address;

    public ShipmentVo() {
    }

    public ShipmentVo(ReceiverVo receiver, AddressVo address) {
        this.receiver = receiver;
        this.address = address;
    }

    public ReceiverVo getReceiver() {
        return receiver;
    }

    public void setReceiver(ReceiverVo receiver) {
        this.receiver = receiver;
    }

    public AddressVo getAddress() {
        return address;
    }

    public void setAddress(AddressVo address) {
        this.address = address;
    }

    public JSONObject getShipmentJson() {
        JSONObject jsonShipment = null;
        try {
            jsonShipment = new JSONObject();
            JSONObject receiverJson = new JSONObject();
            JSONObject addressJson = new JSONObject();

            receiverJson.put("name", this.receiver.getName());
            receiverJson.put("phone", this.receiver.getPhone());
            addressJson.put("add", this.address.getAdd());
            addressJson.put("landmark", this.address.getLandmark());
            addressJson.put("city", this.address.getCity());
            addressJson.put("state", this.address.getState());
            addressJson.put("pin", this.address.getPin());
            addressJson.put("country", this.address.getCountry());

            jsonShipment.put("receiver", receiverJson);
            jsonShipment.put("address", addressJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonShipment;
    }

    public class ReceiverVo {
        private String name;
        private String phone;

        public ReceiverVo() {
        }

        public ReceiverVo(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public class AddressVo {
        private String add;
        private String landmark;
        private String city;
        private String state;
        private String country;
        private int pin;


        public AddressVo() {
        }

        public AddressVo(String add, String landmark, String city, String state, String country, int pin) {
            this.add = add;
            this.landmark = landmark;
            this.city = city;
            this.state = state;
            this.country = country;
            this.pin = pin;
        }

        public String getAdd() {
            return add;
        }

        public void setAdd(String add) {
            this.add = add;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getPin() {
            return pin;
        }

        public void setPin(int pin) {
            this.pin = pin;
        }
    }
}
