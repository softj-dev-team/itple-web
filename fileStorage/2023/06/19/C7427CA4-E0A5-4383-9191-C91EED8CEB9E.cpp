#include <stdio.h>

int main(){
	int N = 0;
	int A = 0;
	int B = 0;
	int C = 0;
	int X = 0;//세로 
	int Y = 0;//가로 
	int row = 0;//세로 
	int col = 0;//가로 
	
	scanf("%d %d",&N,&C);
	A = N;
	B = N;
	for(int i = 0;i<C;i++){
		scanf("%d %d",&X,&Y);
		if(X>=A || Y>=B){
			continue;
		}
		
		row = X*B;
		col = Y*A;
		
		if(row >= col){
			A=X;
		}
		else{
			B=Y;
		}
	}
	printf("%d",A*B);
}
