import java.util.ArrayList;

public class JsonEncoder {
	public static abstract class Value {
		public abstract String encode();
	}
	public static<T> ArrayValue stringList(T[] list) {
		StringValue[] r = new StringValue[list.length];
		for (int i = 0; i < list.length; i++) r[i] = new StringValue(list[i].toString());
		return new ArrayValue(r);
	}
	public static<T> ArrayValue stringList(ArrayList<T> list) {
		StringValue[] r = new StringValue[list.size()];
		for (int i = 0; i < list.size(); i++) r[i] = new StringValue(list.get(i).toString());
		return new ArrayValue(r);
	}
	public static ArrayValue intList(int[] list) {
		IntValue[] r = new IntValue[list.length];
		for (int i = 0; i < list.length; i++) r[i] = new IntValue(list[i]);
		return new ArrayValue(r);
	}
	public static<T extends Value> ArrayValue objectList(ArrayList<T> list) {
		Value[] r = new Value[list.size()];
		for (int i = 0; i < list.size(); i++) r[i] = list.get(i);
		return new ArrayValue(r);
	}
	public static class StringValue extends Value {
		public String v;
		public StringValue(String v) { this.v = v; }
		public String encode() { return "\"" + v + "\""; }
	}
	public static class IntValue extends Value {
		public float v;
		public IntValue(float v) { this.v = v; }
		public String encode() { return "" + v; }
	}
	public static class BooleanValue extends Value {
		public boolean v;
		public BooleanValue(boolean v) { this.v = v; }
		public String encode() { if (v) { return "true"; } else { return "false"; } }
	}
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