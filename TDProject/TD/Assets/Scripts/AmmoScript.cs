using UnityEngine;
using System.Collections;

public class AmmoScript : MonoBehaviour {

    private bool targetAssigned;
    public bool TargetAssigned
    {
        get
        {
            return targetAssigned;
        }
        set
        {
            targetAssigned = value;
        }
    }

    private GameObject target;
    public GameObject Target
    {
        get
        {
            return target;
        }
        set
        {
            target = value;
        }
    }

    private float damage;
    public float Damage
    {
        get
        {
            return damage;
        }
        set
        {
            damage = value;
        }
    }

    private float speed;
    public float Speed
    {
        get
        {
            return speed;
        }
        set
        {
            speed = value;
        }
    }

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        if (!targetAssigned)
        {
            return;
        }

        if (target == null)
        {
            Destroy(gameObject);
            return;
        }
        Vector2 v = target.GetComponent<Transform>().position - gameObject.GetComponent<Transform>().position;
        if (v.magnitude < 0.1f)
        {
            var BS = gameObject.GetComponent<BoomScript>();
            if (BS) { BS.Boom(); }
            else
            {
                var US = target.GetComponent<UnitScript>();
                if (US)
                {
                    US.LostHp(Damage);
                }
            }
            gameObject.GetComponent<SpriteRenderer>().enabled = false;
            
        }
        v.Normalize();
        gameObject.GetComponent<Rigidbody2D>().velocity = v * speed;
	}

    void OnTriggerEnter2D(Collider2D obj)
    {
        
        
        UnitScript US = obj.GetComponent<UnitScript>();
        if (US)
        {
            
            US.LostHp(damage);
            //Destroy(gameObject);
        }
    }
}
