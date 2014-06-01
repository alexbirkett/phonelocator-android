package com.birkettenterprise.phonelocator.model.request;

import java.util.ArrayList;

/**
 * Created by alex on 10/05/14.
 */
public class MessageRequestDO extends ArrayList<MessageRequestDO.Message>{

    public static class Message {
        public static class Location {
            public double latitude;
            public double longitude;
            public double speed;
            public double course;
            public long timestamp;
            public float accuracy;
        }
        public Location location = new Location();
    }

}
