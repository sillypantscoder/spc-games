package com.sillypantscoder.votegame;

import java.util.ArrayList;

public class JsonEncoder {
	/**
	 * The abstract base class for a value that can be encoded.
	 */
	public static abstract class Value {
		public abstract String encode();
	}
	/**
	 * Convert an array of anything to a string list, using toString().
	 * @param <T> The contents of the array.
	 * @param list The list to convert.
	 * @return
	 */
	public static<T> ArrayValue stringList(T[] list) {
		StringValue[] r = new StringValue[list.length];
		for (int i = 0; i < list.length; i++) r[i] = new StringValue(list[i].toString());
		return new ArrayValue(r);
	}
	/**
	 * Convert an ArrayList of anything to a string list, using toString().
	 * @param <T> The contents of the ArrayList.
	 * @param list The list to convert.
	 * @return
	 */
	public static<T> ArrayValue stringList(ArrayList<T> list) {
		StringValue[] r = new StringValue[list.size()];
		for (int i = 0; i < list.size(); i++) r[i] = new StringValue(list.get(i).toString());
		return new ArrayValue(r);
	}
	/**
	 * Convert a list of ints to an ArrayValue.
	 * @param list
	 * @return
	 */
	public static ArrayValue intList(int[] list) {
		IntValue[] r = new IntValue[list.length];
		for (int i = 0; i < list.length; i++) r[i] = new IntValue(list[i]);
		return new ArrayValue(r);
	}
	/**
	 * Convert an ArrayList of Values to an ArrayValue of Values.
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static<T extends Value> ArrayValue objectList(ArrayList<T> list) {
		Value[] r = new Value[list.size()];
		for (int i = 0; i < list.size(); i++) r[i] = list.get(i);
		return new ArrayValue(r);
	}
	/**
	 * A string.
	 */
	public static class StringValue extends Value {
		public String v;
		public StringValue(String v) { this.v = v; }
		public String encode() { return "\"" + v.replaceAll("\n", "\\\\n") + "\""; }
	}
	/**
	 * A number.
	 */
	public static class IntValue extends Value {
		public float v;
		public IntValue(float v) { this.v = v; }
		public String encode() { return "" + v; }
	}
	/**
	 * A boolean.
	 */
	public static class BooleanValue extends Value {
		public boolean v;
		public BooleanValue(boolean v) { this.v = v; }
		public String encode() { if (v) { return "true"; } else { return "false"; } }
	}
	/**
	 * An array containing other Value objects.
	 */
	public static class ArrayValue extends Value {
		public Value[] v;
		public ArrayValue(Value[] v) { this.v = v; }
		public String encode() {
			if (v.length == 0) return "[]";
			String r = "[";
			for (int i = 0; i < v.length; i++) {
				r += v[i].encode();
				if (i != v.length - 1) r += ",";
				else r += "]";
			}
			return r;
		}
	}
	/**
	 * An object containing key-value pairs.
	 */
	public static class ObjectValue extends Value {
		public String[] names;
		public Value[] values;
		public ObjectValue(String[] names, Value[] values) { this.names = names; this.values = values; }
		public String encode() {
			String r = "{";
			for (int i = 0; i < names.length; i++) {
				r += "\"" + names[i] + "\":" + values[i].encode();
				if (i != names.length - 1) r += ",";
				else r += "}";
			}
			return r;
		}
	}
}