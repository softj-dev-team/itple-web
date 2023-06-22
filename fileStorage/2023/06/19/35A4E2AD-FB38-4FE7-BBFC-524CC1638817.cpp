#include <stdio.h>

int main(){
	int N = 0;
	int arr[250000] = {0};
	int top = 1;
	int cnt = 0;
	
	scanf("%d",&N);
	for(int i = 0;i<N;i++){
		scanf("%d",&arr[i]);
	}
	for(int i = 0;i<N;i++){
		if(arr[i]==top){
			top++;
			continue;
		}//6 1 2 3 2 4 5 10
		else if(arr[i]!=top){
			cnt ++;
		}
	}
	printf("%d",cnt);
}
