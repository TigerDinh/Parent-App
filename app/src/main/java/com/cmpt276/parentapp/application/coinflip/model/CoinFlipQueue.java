package com.cmpt276.parentapp.application.coinflip.model;

import com.cmpt276.parentapp.application.children.model.Child;

import java.util.ArrayList;
import java.util.List;

public class CoinFlipQueue {

    public static final Child ANONYMOUS = new Child("anonymous", null, null);
    private final List<Child> children;
    private Child candidate;

    public CoinFlipQueue(List<Child> children) {
        this.children = new ArrayList<>(children);
        candidate = children.isEmpty() ? ANONYMOUS : children.get(0);
    }

    /** Get the candidate to flip the coin
     * Called by CoinFlipActivity
     *
     * @return Child to flip the coin
     */
    public Child getCandidate() {
        validateInvariant();
        return candidate;
    }

    private void validateInvariant() {
        if (candidate == null || !candidate.equals(ANONYMOUS) && !children.contains(candidate)) {
            throw new RuntimeException("CoinFlipQueue candidate invariant violation.");
        }
    }

    /**
     * set candidate to nobody
     * (This should be carefully used to keep the invariant)
     */
    public void cleanCandidate() {
        candidate = ANONYMOUS;
    }

    /** Called from QueueActivity to set the candidate
     *
     * @param index of the candidate in the queue
     */
    public void setCandidate(int index) {
        assert(index >= 0 && index < children.size());
        candidate = children.get(index);
    }

    /** Put child just flipped the coin into the end of the queue
     * Called by CoinFlipActivity
     *
     * @param child to put to then end
     */
    public void pushBack(Child child) {
        assert(candidate.equals(child));
        if (child == ANONYMOUS) {
            return;
        }
        assert(this.children.contains(child));
        this.children.remove(child);
        this.children.add(child);
        candidate = this.children.get(0);
    }

    public ArrayList<String> view() {
        ArrayList<String> names = new ArrayList<>();
        for (Child child : this.children) {
            names.add(child.getName());
        }
        return names;
    }

    /**
     * ChildManager should update Queue when child is added.
     * @param child
     */
    public void add(Child child) {
        this.children.add(child);
    }

    /**
     * ChildManager should update Queue when child is removed.
     * @param child
     */
    public void remove(Child child) {
        this.children.remove(child);
        if (candidate.equals(child)) {
            candidate = this.children.isEmpty() ? ANONYMOUS : this.children.get(0);
        }
    }

    /** ChildManager should update Queue when child is replaced.
     *
     * @param oldChild
     * @param newChild
     */
    public void replace(Child oldChild, Child newChild) {
        assert(children.contains(oldChild));
        assert(!ANONYMOUS.equals(newChild));

        children.set(children.indexOf(oldChild), newChild);
        if (candidate.equals(oldChild)) {
            candidate = newChild;
        }
    }

    public List<Child> getChildren() {
        return children;
    }
}
