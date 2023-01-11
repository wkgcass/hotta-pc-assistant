package net.cassite.hottapcassistant.component.loading;

public record LoadingItem(int weight, String name, Runnable loadFunc) {
}
