package com.sincress.trashmaster;

/**
 * Describes a marker entry in the database, consisting of:
 * Marker type (0-4)
 * Number of downvotes and upvotes
 * Marker global coordintes
 */
public class MarkerEntry {
    public int type, upvotes, downvotes;
    public double latitude, longitude;
}
