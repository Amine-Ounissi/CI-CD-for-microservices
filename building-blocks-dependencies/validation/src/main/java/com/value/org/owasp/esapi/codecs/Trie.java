package com.value.org.owasp.esapi.codecs;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public interface Trie<T> extends Map<CharSequence, T> {
  Entry<CharSequence, T> getLongestMatch(CharSequence paramCharSequence);
  
  Entry<CharSequence, T> getLongestMatch(PushbackReader paramPushbackReader) throws IOException;
  
  int getMaxKeyLength();
  
  public static class TrieProxy<T> implements Trie<T> {
    private Trie<T> wrapped;
    
    TrieProxy(Trie<T> toWrap) {
      this.wrapped = toWrap;
    }
    
    protected Trie<T> getWrapped() {
      return this.wrapped;
    }
    
    public Entry<CharSequence, T> getLongestMatch(CharSequence key) {
      return this.wrapped.getLongestMatch(key);
    }
    
    public Entry<CharSequence, T> getLongestMatch(PushbackReader keyIn) throws IOException {
      return this.wrapped.getLongestMatch(keyIn);
    }
    
    public int getMaxKeyLength() {
      return this.wrapped.getMaxKeyLength();
    }
    
    public int size() {
      return this.wrapped.size();
    }
    
    public boolean isEmpty() {
      return this.wrapped.isEmpty();
    }
    
    public boolean containsKey(Object key) {
      return this.wrapped.containsKey(key);
    }
    
    public boolean containsValue(Object val) {
      return this.wrapped.containsValue(val);
    }
    
    public T get(Object key) {
      return this.wrapped.get(key);
    }
    
    public T put(CharSequence key, T value) {
      return this.wrapped.put(key, value);
    }
    
    public T remove(Object key) {
      return this.wrapped.remove(key);
    }
    
    public void putAll(Map<? extends CharSequence, ? extends T> t) {
      this.wrapped.putAll(t);
    }
    
    public void clear() {
      this.wrapped.clear();
    }
    
    public Set<CharSequence> keySet() {
      return this.wrapped.keySet();
    }
    
    public Collection<T> values() {
      return this.wrapped.values();
    }
    
    public Set<Entry<CharSequence, T>> entrySet() {
      return this.wrapped.entrySet();
    }
    
    public boolean equals(Object other) {
      return this.wrapped.equals(other);
    }
    
    public int hashCode() {
      return this.wrapped.hashCode();
    }
  }
  
  public static class Unmodifiable<T> extends TrieProxy<T> {
    Unmodifiable(Trie<T> toWrap) {
      super(toWrap);
    }
    
    public T put(CharSequence key, T value) {
      throw new UnsupportedOperationException("Unmodifiable Trie");
    }
    
    public T remove(CharSequence key) {
      throw new UnsupportedOperationException("Unmodifiable Trie");
    }
    
    public void putAll(Map<? extends CharSequence, ? extends T> t) {
      throw new UnsupportedOperationException("Unmodifiable Trie");
    }
    
    public void clear() {
      throw new UnsupportedOperationException("Unmodifiable Trie");
    }
    
    public Set<CharSequence> keySet() {
      return Collections.unmodifiableSet(super.keySet());
    }
    
    public Collection<T> values() {
      return Collections.unmodifiableCollection(super.values());
    }
    
    public Set<Entry<CharSequence, T>> entrySet() {
      return Collections.unmodifiableSet(super.entrySet());
    }
  }
  
  public static class Util {
    static <T> Trie<T> unmodifiable(Trie<T> toWrap) {
      return new Unmodifiable<>(toWrap);
    }
  }
}
