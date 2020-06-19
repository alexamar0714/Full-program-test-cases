package ru.otus.l62.b2.visitor;

/**
 * Created by tully.
 */
public class CarWashService implements Service {
    @Override
    public void visit(Wheel element) {
        System.out.println("Washing: " + element.getName());
    }

    @Override
    public void visit(Engine element) {
        System.out.println("Washing: " + element.getName());
    }

    @Override
    public void visit(Body element) {
        System.out.println("Washing: " + element.getName());
    }
}
