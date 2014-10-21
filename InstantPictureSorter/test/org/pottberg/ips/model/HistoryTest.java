package org.pottberg.ips.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class HistoryTest {

    @Test
    public void testAddToHistoryOneItem() {
	History<Integer> history = new History<>();
	history.addToHistory(1);
	assertEquals(new Integer(1), history.getPrevious());
	assertNull(history.getNext());
    }
    
    @Test
    public void testAddToHistoryMultipleItems() {
	History<Integer> history = new History<>();
	history.addToHistory(1);
	history.addToHistory(2);
	history.addToHistory(3);
	assertEquals(new Integer(3), history.getPrevious());
	assertNull(history.getNext());
    }

    @Test
    public void testAddToFutureOneItem() {
	History<Integer> history = new History<>();
	history.addToFuture(1);
	assertNull(history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
    }
    
    @Test
    public void testAddToFutureMultipleItems() {
	History<Integer> history = new History<>();
	history.addToFuture(1);
	history.addToFuture(2);
	history.addToFuture(3);
	assertNull(history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
    }
    
    @Test
    public void testAddMultipleItems() {
	History<Integer> history = createFilledHistory();
	assertEquals(new Integer(-1), history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
    }

    @Test
    public void testHasPrevious() {
	History<Integer> history = new History<>();
	assertFalse(history.hasPrevious());
	history.addToHistory(1);
	assertTrue(history.hasPrevious());
    }

    @Test
    public void testBack() {
	History<Integer> history = createFilledHistory();
	assertEquals(new Integer(-1), history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
	history.back();
	assertEquals(new Integer(-2), history.getPrevious());
	assertEquals(new Integer(-1), history.getNext());
	history.back();
	assertEquals(new Integer(-3), history.getPrevious());
	assertEquals(new Integer(-2), history.getNext());
	history.back();
	assertNull(history.getPrevious());
	assertEquals(new Integer(-3), history.getNext());
	history.back();
	assertNull(history.getPrevious());
	assertEquals(new Integer(-3), history.getNext());
    }

    @Test
    public void testNext() {
	History<Integer> history = createFilledHistory();
	assertEquals(new Integer(-1), history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
	history.next();
	assertEquals(new Integer(1), history.getPrevious());
	assertEquals(new Integer(2), history.getNext());
	history.next();
	assertEquals(new Integer(2), history.getPrevious());
	assertEquals(new Integer(3), history.getNext());
	history.next();
	assertEquals(new Integer(3), history.getPrevious());
	assertNull(history.getNext());
	history.next();
	assertEquals(new Integer(3), history.getPrevious());
	assertNull(history.getNext());
    }

    @Test
    public void testHasNext() {
	History<Integer> history = new History<>();
	assertFalse(history.hasNext());
	history.addToFuture(1);
	assertTrue(history.hasNext());
    }

    @Test
    public void testSize() {
	History<Integer> history = new History<>();
	assertEquals(0, history.size());
        history.addToHistory(-3);
        assertEquals(1, history.size());
        history.addToHistory(-2);
        assertEquals(2, history.size());
        history.addToHistory(-1);
        assertEquals(3, history.size());
        history.addToFuture(1);
        assertEquals(4, history.size());
        history.addToFuture(2);
        assertEquals(5, history.size());
        history.addToFuture(3);
        assertEquals(6, history.size());
    }

    @Test
    public void testGetPreviousIndex() {
	History<Integer> history = createFilledHistory();
	assertEquals(2, history.getPreviousIndex());
	history.next();
	assertEquals(3, history.getPreviousIndex());
	history.next();
	assertEquals(4, history.getPreviousIndex());
	history.next();
	assertEquals(5, history.getPreviousIndex());
	history.next();
	assertEquals(5, history.getPreviousIndex());
    }

    @Test
    public void testGetLastIndex() {
	History<Integer> history = new History<>();
	assertEquals(-1, history.getLastIndex());
        history.addToHistory(-3);
        assertEquals(0, history.getLastIndex());
        history.addToHistory(-2);
        assertEquals(1, history.getLastIndex());
        history.addToHistory(-1);
        assertEquals(2, history.getLastIndex());
        history.addToFuture(1);
        assertEquals(3, history.getLastIndex());
        history.addToFuture(2);
        assertEquals(4, history.getLastIndex());
        history.addToFuture(3);
        assertEquals(5, history.getLastIndex());
    }
    
    @Test
    public void testDeleteFuture() {
	History<Integer> history = createFilledHistory();
	assertEquals(3 + 3, history.size());
	assertEquals(new Integer(-1), history.getPrevious());
	assertEquals(new Integer(1), history.getNext());
	history.addToHistory(42);
	assertEquals(3 + 1, history.size());
	assertEquals(new Integer(42), history.getPrevious());
	assertNull(history.getNext());
    }

    private History<Integer> createFilledHistory() {
        History<Integer> history = new History<>();
        history.addToHistory(-3);
        history.addToHistory(-2);
        history.addToHistory(-1);
        history.addToFuture(1);
        history.addToFuture(2);
        history.addToFuture(3);
        return history;
    }

}
