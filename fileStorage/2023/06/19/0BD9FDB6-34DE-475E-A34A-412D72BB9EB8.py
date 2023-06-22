n=int(input())

beehouse = 1
room = 0

while n>=beehouse:
    beehouse+= 6*room
    room+=1

    #print(room)
print (room)
