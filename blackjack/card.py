class Card(object):
    """ represents a card with suite and rank """
    """ Attributes """
    suit_names = ["Clubs", "Diamonds", "Hearts", "Spades"]
    rank_names = ["None", "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"]
    
    def __init__(self, suit, rank):
        if(suit>=0 and suit<=3): 
            self.suit = suit
        else:
            raise ValueError, 'invalid value passed for suit: '+suit+' valid values are from 0-3'
            
        if(rank>0 and rank<14):
            self.rank = rank
        else:
            raise ValueError, 'invalid value passed for rank: '+rank+' valid values are from 1-13'
    
    def __str__(self):
        return "%s of %s" % (self.suit_names[self.suit], self.rank_names[self.rank])
        
    def __cmp__(self, other):
        t1 = self.suite, self.rank
        t2 = other.suite, other.rank
        return cmp(t1, t2)
        
    def get_rank(self):
        return self.rank
    
    def get_suite(self):
        return self.suite
    

