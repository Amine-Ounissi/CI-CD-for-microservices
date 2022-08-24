package com.value.org.owasp.esapi.codecs;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashTrie<T> implements Trie<T> {
  private Node<T> root;
  
  private int maxKeyLen;
  
  private int size;
  
  private static class Entry<T> implements Map.Entry<CharSequence, T> {
    private CharSequence key;
    
    private T value;
    
    Entry(CharSequence key, T value) {
      this.key = key;
      this.value = value;
    }
    
    static <T> Entry<T> newInstanceIfNeeded(CharSequence key, int keyLength, T value) {
      if (value == null || key == null)
        return null; 
      if (key.length() > keyLength)
        key = key.subSequence(0, keyLength); 
      return new Entry<>(key, value);
    }
    
    static <T> Entry<T> newInstanceIfNeeded(CharSequence key, T value) {
      if (value == null || key == null)
        return null; 
      return new Entry<>(key, value);
    }
    
    public CharSequence getKey() {
      return this.key;
    }
    
    public T getValue() {
      return this.value;
    }
    
    public T setValue(T value) {
      throw new UnsupportedOperationException();
    }
    
    public boolean equals(Map.Entry other) {
      return (NullSafe.equals(this.key, other.getKey()) && NullSafe.equals(this.value, other.getValue()));
    }
    
    public boolean equals(Object o) {
      if (o instanceof Map.Entry)
        return equals((Map.Entry)o); 
      return false;
    }
    
    public int hashCode() {
      return NullSafe.hashCode(this.key) ^ NullSafe.hashCode(this.value);
    }
    
    public String toString() {
      return NullSafe.toString(this.key) + " => " + NullSafe.toString(this.value);
    }
  }
  
  private static class Node<T> {
    private T value = null;
    
    private Map<Character, Node<T>> nextMap;
    
    private static <T> Map<Character, Node<T>> newNodeMap() {
      return new HashMap<>();
    }
    
    private static <T> Map<Character, Node<T>> newNodeMap(Map<Character, Node<T>> prev) {
      return new HashMap<>(prev);
    }
    
    void setValue(T value) {
      this.value = value;
    }
    
    Node<T> getNextNode(Character ch) {
      if (this.nextMap == null)
        return null; 
      return this.nextMap.get(ch);
    }
    
    T put(CharSequence key, int pos, T addValue) {
      Node<T> nextNode;
      if (key.length() == pos) {
        T old = this.value;
        setValue(addValue);
        return old;
      } 
      Character ch = Character.valueOf(key.charAt(pos));
      if (this.nextMap == null) {
        this.nextMap = newNodeMap();
        nextNode = new Node();
        this.nextMap.put(ch, nextNode);
      } else if ((nextNode = this.nextMap.get(ch)) == null) {
        nextNode = new Node();
        this.nextMap.put(ch, nextNode);
      } 
      return nextNode.put(key, pos + 1, addValue);
    }
    
    T get(CharSequence key, int pos) {
      if (key.length() <= pos)
        return this.value; 
      Node<T> nextNode;
      if ((nextNode = getNextNode(Character.valueOf(key.charAt(pos)))) == null)
        return null; 
      return nextNode.get(key, pos + 1);
    }
    
    Entry<T> getLongestMatch(CharSequence key, int pos) {
      if (key.length() <= pos)
        return Entry.newInstanceIfNeeded(key, this.value);
      Node<T> nextNode;
      if ((nextNode = getNextNode(Character.valueOf(key.charAt(pos)))) == null)
        return Entry.newInstanceIfNeeded(key, pos, this.value);
      Entry<T> ret;
      if ((ret = nextNode.getLongestMatch(key, pos + 1)) != null)
        return ret; 
      return Entry.newInstanceIfNeeded(key, pos, this.value);
    }
    
    Entry<T> getLongestMatch(PushbackReader keyIn, StringBuilder key) throws IOException {
      int c;
      if ((c = keyIn.read()) < 0)
        return Entry.newInstanceIfNeeded(key, this.value);
      char ch = (char)c;
      int prevLen = key.length();
      key.append(ch);
      Node<T> nextNode;
      if ((nextNode = getNextNode(Character.valueOf(ch))) == null)
        return Entry.newInstanceIfNeeded(key, this.value);
      Entry<T> ret;
      if ((ret = nextNode.getLongestMatch(keyIn, key)) != null)
        return ret; 
      key.setLength(prevLen);
      keyIn.unread(c);
      return Entry.newInstanceIfNeeded(key, this.value);
    }
    
    void remap() {
      if (this.nextMap == null)
        return; 
      this.nextMap = newNodeMap(this.nextMap);
      for (Node<T> node : this.nextMap.values())
        node.remap(); 
    }
    
    boolean containsValue(Object toFind) {
      if (this.value != null && toFind.equals(this.value))
        return true; 
      if (this.nextMap == null)
        return false; 
      for (Node<T> node : this.nextMap.values()) {
        if (node.containsValue(toFind))
          return true; 
      } 
      return false;
    }
    
    Collection<T> values(Collection<T> values) {
      if (this.value != null)
        values.add(this.value); 
      if (this.nextMap == null)
        return values; 
      for (Node<T> node : this.nextMap.values())
        node.values(values); 
      return values;
    }
    
    Set<CharSequence> keySet(StringBuilder key, Set<CharSequence> keys) {
      int len = key.length();
      if (this.value != null)
        keys.add(key.toString()); 
      if (this.nextMap != null && this.nextMap.size() > 0) {
        key.append('X');
        for (Map.Entry<Character, Node<T>> entry : this.nextMap.entrySet()) {
          key.setCharAt(len, ((Character)entry.getKey()).charValue());
          ((Node)entry.getValue()).keySet(key, keys);
        } 
        key.setLength(len);
      } 
      return keys;
    }
    
    Set<Map.Entry<CharSequence, T>> entrySet(StringBuilder key, Set<Map.Entry<CharSequence, T>> entries) {
      int len = key.length();
      if (this.value != null)
        entries.add(new Entry<>(key.toString(), this.value));
      if (this.nextMap != null && this.nextMap.size() > 0) {
        key.append('X');
        for (Map.Entry<Character, Node<T>> entry : this.nextMap.entrySet()) {
          key.setCharAt(len, ((Character)entry.getKey()).charValue());
          ((Node<T>)entry.getValue()).entrySet(key, entries);
        } 
        key.setLength(len);
      } 
      return entries;
    }
    
    private Node() {}
  }
  
  public HashTrie() {
    clear();
  }
  
  public Map.Entry<CharSequence, T> getLongestMatch(CharSequence key) {
    if (this.root == null || key == null)
      return null; 
    return this.root.getLongestMatch(key, 0);
  }
  
  public Map.Entry<CharSequence, T> getLongestMatch(PushbackReader keyIn) throws IOException {
    if (this.root == null || keyIn == null)
      return null; 
    return this.root.getLongestMatch(keyIn, new StringBuilder());
  }
  
  public int getMaxKeyLength() {
    return this.maxKeyLen;
  }
  
  public void clear() {
    this.root = null;
    this.maxKeyLen = -1;
    this.size = 0;
  }
  
  public boolean containsKey(Object key) {
    return (get(key) != null);
  }
  
  public boolean containsValue(Object value) {
    if (this.root == null)
      return false; 
    return this.root.containsValue(value);
  }
  
  public T put(CharSequence key, T value) throws NullPointerException {
    if (key == null)
      throw new NullPointerException("Null keys are not handled"); 
    if (value == null)
      throw new NullPointerException("Null values are not handled"); 
    if (this.root == null)
      this.root = new Node<>(); 
    T old;
    if ((old = this.root.put(key, 0, value)) != null)
      return old; 
    int len;
    if ((len = key.length()) > this.maxKeyLen)
      this.maxKeyLen = len; 
    this.size++;
    return null;
  }
  
  public T remove(Object key) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
  
  public void putAll(Map<? extends CharSequence, ? extends T> map) {
    for (Map.Entry<? extends CharSequence, ? extends T> entry : map.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public Set<CharSequence> keySet() {
    Set<CharSequence> keys = new HashSet<>(this.size);
    if (this.root == null)
      return keys; 
    return this.root.keySet(new StringBuilder(), keys);
  }
  
  public Collection<T> values() {
    ArrayList<T> values = new ArrayList<>(size());
    if (this.root == null)
      return values; 
    return this.root.values(values);
  }
  
  public Set<Map.Entry<CharSequence, T>> entrySet() {
    Set<Map.Entry<CharSequence, T>> entries = new HashSet<>(size());
    if (this.root == null)
      return entries; 
    return this.root.entrySet(new StringBuilder(), entries);
  }
  
  public T get(Object key) {
    if (this.root == null || key == null)
      return null; 
    if (!(key instanceof CharSequence))
      return null; 
    return this.root.get((CharSequence)key, 0);
  }
  
  public int size() {
    return this.size;
  }
  
  public boolean equals(Object other) {
    if (other == null)
      return false; 
    if (!(other instanceof Map))
      return false; 
    return entrySet().equals(((Map)other).entrySet());
  }
  
  public int hashCode() {
    return entrySet().hashCode();
  }
  
  public String toString() {
    if (isEmpty())
      return "{}"; 
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    sb.append("{ ");
    for (Map.Entry<CharSequence, T> entry : entrySet()) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      } 
      sb.append(entry.toString());
    } 
    sb.append(" }");
    return sb.toString();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
}
